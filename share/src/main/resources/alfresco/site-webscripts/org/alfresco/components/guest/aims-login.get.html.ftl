
<@markup id="html">
   <@uniqueIdDiv>
      <@markup id="preloader">
         <script type="text/javascript">//<![CDATA[
            var urlParams = new URLSearchParams(window.location.search);
            var redirectUrl = urlParams.get('redirectUrl');

            if (redirectUrl == null || redirectUrl.length == 0) {
                window.location.replace("/");
            }

            var query = window.location.search;
            if (window.location.hash) {
                var fragment = window.location.hash.substring(1);
                query += (window.location.search ? '&' : '?') + 'fragment=' + encodeURIComponent(fragment);
            }
            window.location.replace("aims-dologin" + query);
         //]]></script>
      </@markup>
   </@>
</@>