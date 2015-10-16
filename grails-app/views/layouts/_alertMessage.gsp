<%
    /*
     * flash.messageType- set this to display different type of alert blocks, eg.- success/error/info etc.
     * flash.timeout- set the time in milli seconds to hide the alert block. Default: 10000 milli seconds.
     */
 %>

<g:set var="classes" value="${flash.messageType ? 'alert-' + flash.messageType : 'alert-info' } ${flash.message ? ''
        : 'hide' }" />
<div id="alert-message" class="alert ${classes } text-center in">
    <button type="button" class="close" onclick="$(this).parent().slideUp()" aria-label="Close">&times;</button>
    <strong>${flash.message}</strong>
</div>

<g:if test="${flash.message && (!flash.timeout || (flash.timeout && flash.timeout != 'clear')) }">
    <asset:script>
        setTimeout(function() {
            $('div#alert-message').slideUp();
        }, ${flash.timeout ?: '10000' });
    </asset:script>
</g:if>