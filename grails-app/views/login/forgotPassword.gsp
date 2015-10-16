<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<meta name="layout" content="main"/>
	<title>Forgot Password - Ōura Ring</title>
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
						Forgot Password
					</h2>

					<p class="text-muted">
						Type in the email address or username you used when you registered with Ōura Ring. We'll
						email you the instructions to reset your password.
					</p>

					<g:form action="forgotPassword" method="POST">
						<div class="form-group has-feedback feedback-left">
							<g:textField class="form-control input-lg" name="username" value="${params.username}"
									placeholder="Enter your username" required="" autofocus="" />
							<i class="fa fa-user form-control-feedback"></i>
						</div>
						<br>

						<button class="btn btn-green btn-flat btn-lg btn-block" type="submit">
							<strong>SEND ME INSTRUCTIONS</strong>
						</button>
					</g:form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>