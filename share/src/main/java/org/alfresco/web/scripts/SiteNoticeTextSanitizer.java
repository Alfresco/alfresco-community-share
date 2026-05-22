/*
 * Copyright 2005 - 2026 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.scripts;

import java.util.regex.Pattern;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * HTML sanitiser for the Site Notice dashlet text body.
 *
 * <p>Uses a custom OWASP policy aligned with the dashlet's TinyMCE
 * {@code extended_valid_elements} config. The generic
 * {@code stringUtils.stripUnsafeHTML} is not used because it strips
 * {@code target} from anchors, regressing MNT-24073 / MNT-25236.</p>
 *
 * <p>Allows: formatting (b, i, u, s, font, span, div, etc.), lists, tables,
 * images, anchors with {@code href}/{@code target}/{@code name},
 * {@code class}, {@code align}, {@code style} (OWASP CSS-parsed).</p>
 *
 * <p>Removes: everything else (script, iframe, event handlers,
 * {@code javascript:} URIs, dangerous CSS, etc.).</p>
 *
 * <p>Called from {@code site-notice.get.js} via
 * {@code Packages.org.alfresco.web.scripts.SiteNoticeTextSanitizer.sanitize(...)}.</p>
 */
public final class SiteNoticeTextSanitizer
{
    /** Only the four standard window targets are kept. */
    private static final Pattern TARGET_PATTERN =
        Pattern.compile("(?i)^(_blank|_self|_top|_parent)$");

    /**
     * The shared, thread-safe policy. {@link PolicyFactory} instances are
     * documented as immutable and safe to share.
     */
    private static final PolicyFactory POLICY =
          Sanitizers.FORMATTING
        .and(Sanitizers.BLOCKS)
        .and(Sanitizers.STYLES)
        .and(Sanitizers.TABLES)
        .and(new HtmlPolicyBuilder()
            // Additional formatting elements beyond Sanitizers.FORMATTING
            .allowElements("u", "s", "strike", "small", "big", "code",
                           "pre", "hr", "font", "span", "div")

            // Anchors — explicit policy that keeps target/name/rel/title
            .allowElements("a")
            .allowStandardUrlProtocols()
            .allowAttributes("href").onElements("a")
            .allowAttributes("target").matching(TARGET_PATTERN).onElements("a")
            .allowAttributes("name", "rel", "title").onElements("a")

            // Images
            .allowElements("img")
            .allowAttributes("src").onElements("img")
            .allowAttributes("alt", "width", "height", "title").onElements("img")

            // Generic visual attributes used by TinyMCE
            .allowAttributes("class", "align").globally()

            // Legacy <font> attributes
            .allowAttributes("face", "size", "color").onElements("font")

            .toFactory());

    private SiteNoticeTextSanitizer()
    {
        // Utility class.
    }

    /**
     * Sanitise the Site Notice text body.
     *
     * @param html the raw, possibly attacker-controlled HTML from
     *             {@code component.properties["text"]}; may be {@code null}.
     * @return a safe HTML fragment with all script / event-handler /
     *         dangerous-URI vectors removed and formatting preserved.
     *         Never {@code null}; an empty input returns {@code ""}.
     */
    public static String sanitize(String html)
    {
        if (html == null || html.isEmpty())
        {
            return "";
        }
        return POLICY.sanitize(html);
    }
}
