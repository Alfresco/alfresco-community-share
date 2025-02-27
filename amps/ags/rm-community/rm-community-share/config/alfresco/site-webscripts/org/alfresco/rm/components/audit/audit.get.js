<import resource="classpath:alfresco/site-webscripts/org/alfresco/rm/components/console/rm-console.lib.js">

/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2025 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

/**
 * Converts an ISO8601-formatted date into a JavaScript native Date object
 *
 * Original code:
 *    dojo.date.stamp.fromISOString
 *    Copyright (c) 2005-2008, The Dojo Foundation
 *    All rights reserved.
 *    BSD license (http://trac.dojotoolkit.org/browser/dojo/trunk/LICENSE)
 *
 * @method Alfresco.thirdparty.fromISO8601
 * @param formattedString {string} ISO8601-formatted date string
 * @return {Date|null}
 * @static
 */
fromISO8601 = function()
{
   var fromISOString = function()
   {
      //	summary:
      //		Returns a Date object given a string formatted according to a subset of the ISO-8601 standard.
      //
      //	description:
      //		Accepts a string formatted according to a profile of ISO8601 as defined by
      //		[RFC3339](http://www.ietf.org/rfc/rfc3339.txt), except that partial input is allowed.
      //		Can also process dates as specified [by the W3C](http://www.w3.org/TR/NOTE-datetime)
      //		The following combinations are valid:
      //
      //			* dates only
      //			|	* yyyy
      //			|	* yyyy-MM
      //			|	* yyyy-MM-dd
      // 			* times only, with an optional time zone appended
      //			|	* THH:mm
      //			|	* THH:mm:ss
      //			|	* THH:mm:ss.SSS
      // 			* and "datetimes" which could be any combination of the above
      //
      //		timezones may be specified as Z (for UTC) or +/- followed by a time expression HH:mm
      //		Assumes the local time zone if not specified.  Does not validate.  Improperly formatted
      //		input may return null.  Arguments which are out of bounds will be handled
      //		by the Date constructor (e.g. January 32nd typically gets resolved to February 1st)
      //		Only years between 100 and 9999 are supported.
      //
      //	formattedString:
      //		A string such as 2005-06-30T08:05:00-07:00 or 2005-06-30 or T08:05:00

      var isoRegExp = /^(?:(\d{4})(?:-(\d{2})(?:-(\d{2}))?)?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(.\d+)?)?((?:[+-](\d{2}):(\d{2}))|Z)?)?$/;

      return function(formattedString)
      {
      	var match = isoRegExp.exec(formattedString);
      	var result = null;

      	if (match)
      	{
      		match.shift();
      		if (match[1]){match[1]--;} // Javascript Date months are 0-based
      		if (match[6]){match[6] *= 1000;} // Javascript Date expects fractional seconds as milliseconds

      		result = new Date(match[0]||1970, match[1]||0, match[2]||1, match[3]||0, match[4]||0, match[5]||0, match[6]||0);

      		var offset = 0;
      		var zoneSign = match[7] && match[7].charAt(0);
      		if (zoneSign != 'Z')
      		{
      			offset = ((match[8] || 0) * 60) + (Number(match[9]) || 0);
      			if (zoneSign != '-')
      			{
      			   offset *= -1;
      			}
      		}
      		if (zoneSign)
      		{
      			offset -= result.getTimezoneOffset();
      		}
      		if (offset)
      		{
      			result.setTime(result.getTime() + offset * 60000);
      		}
      	}

      	return result; // Date or null
      };
   }();

   return fromISOString.apply(arguments.callee, arguments);
};
function main()
{
   var nodeRef = null;
   if (page.url.args.nodeRef)
   {
      model.nodeRef = page.url.args.nodeRef.replace(':/','');
   }
   var nodeRefAuditURI = '/api/node/'+ model.nodeRef +'/rmauditlog';
   var auditURI = "/api/rma/admin/rmauditlog";
   model.uri = (model.nodeRef) ? nodeRefAuditURI : auditURI;
   var result = remote.call((model.nodeRef) ? nodeRefAuditURI : auditURI);
   if (result.status == 200)
   {
      var data = eval('(' + result + ')').data;

      data.startedDate=fromISO8601(data.started);
      data.stoppedDate=fromISO8601(data.stopped);
      model.data=data.toSource();
      for (var i=0,len=data.entries.length;i<len;i++)
      {
         data.entries[i].timestampDate = fromISO8601(data.entries[i].timestamp);
         var displayPath = data.entries[i].path.substring(data.entries[i].path.indexOf("/Sites"));
         data.entries[i].displayPath = displayPath.replace('/Sites', '');
      }
      model.auditStatus = data;
   }
   else
   {
      status.setCode(result.status,eval('(' + result + ')').message);
      return;
   }

   model.capabilities = getCapabilities(remote.connect("alfresco")).toSource();
}

main();
