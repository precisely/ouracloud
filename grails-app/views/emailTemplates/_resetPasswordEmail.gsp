<g:applyLayout name="emailMain">
	<h3 style="margin: 0;">Forgot your password,</h3><br>

	<div>
		You (or someone pretending to be you) requested that your password be reset. If you didn't make this request
		then ignore the email, no changes have been made to your account.
		If you did make the request, then click <g:link controller="login" action="resetPassword" params="[t: token]"
			absolute="true">here</g:link> to reset your password.
	</div><br>
	<div style="color: #777">
		<strong>Ōura Ring Support</strong>
	</div>
</g:applyLayout>