<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<meta name="layout" content="main"/>
	<title>Reset Password - Ōura Ring</title>
</head>
<body>
<div class="container page-wrapper">
	<div class="row df-margin-vertical df-gutter-vertical">
		<div class="col-sm-8 col-sm-push-2 col-md-4 col-md-push-4">
			<div class="well-wrapper login-panel">
				<h2 class="title" style="margin-top: 0;">
					<small class="pull-left">
						<a href="/login" class="text-green" title="Log In"><i class="fa fa-arrow-left"
								style="margin-top: 5px"></i></a>
					</small>
					Reset Password
				</h2>

				<g:form action="resetPassword" method="POST">
					<g:hiddenField name="t" value="${token}" />
					<div class="form-group has-feedback feedback-left ${hasErrors(bean: command, field: 'password',
							'has-error')}">
						<g:passwordField class="form-control input-lg" name="password" autofocus="" required=""
								placeholder="Enter new password" value="${command?.password}" />
						<i class="fa fa-key form-control-feedback"></i>

						<g:hasErrors bean="${command}" field="password">
							<span class="help-block"><g:fieldError field="password" bean="${command}" /></span>
						</g:hasErrors>
					</div>

					<div class="form-group has-feedback feedback-left ${hasErrors(bean: command, field:
							'password2', 'has-error')}">
						<g:passwordField class="form-control input-lg" name="password2" required=""
								placeholder="Confirm password" value="${command?.password2}" />
						<i class="fa fa-key form-control-feedback"></i>

						<g:hasErrors bean="${command}" field="password2">
							<span class="help-block"><g:fieldError field="password2" bean="${command}" /></span>
						</g:hasErrors>
					</div>
					<br>

					<button class="btn btn-green btn-flat btn-lg btn-block" type="submit">
						<strong>RESET MY PASSWORD</strong>
					</button>
				</g:form>
			</div>
		</div>
	</div>
</div>
</body>
</html>