<html>
<head>
	<meta name="layout" content="ring"/>
	<title>Sign Up - Ōura Ring</title>
</head>
<body class="no-headline">
<div class="row">
	<div class="col-sm-8 col-sm-push-2">
		<div class="well-wrapper login-panel">
			<div class="btn-group btn-group-justified">
				<a href="/signup" class="btn btn-block btn-lg btn-flat btn-green">Sign Up</a>
				<a href="/login" class="btn btn-block btn-lg btn-flat btn-default">Log In</a>
			</div>
			<h2 class="title">Get Started</h2>

			<g:form action="signup" method="POST">
				<div class="form-group has-feedback feedback-left ${hasErrors(bean: userInstance, field: 'email',
						'has-error')}">
					<g:field type="email" class="form-control input-lg" name="email" placeholder="Enter your email"
							value="${userInstance?.email}" required="" autofocus="" />
					<i class="fa fa-envelope-o form-control-feedback"></i>

					<g:hasErrors bean="${userInstance}" field="email">
						<span class="help-block"><g:fieldError field="email" bean="${userInstance}" /></span>
					</g:hasErrors>
				</div>

				<div class="form-group has-feedback feedback-left ${hasErrors(bean: userInstance, field: 'username',
						'has-error')}">
					<g:textField class="form-control input-lg" name="username" placeholder="Enter your username"
							value="${userInstance?.username}" required="" />
					<i class="fa fa-user form-control-feedback"></i>

					<g:hasErrors bean="${userInstance}" field="username">
						<span class="help-block"><g:fieldError field="username" bean="${userInstance}" /></span>
					</g:hasErrors>
				</div>

				<div class="form-group has-feedback feedback-left">
					<g:passwordField class="form-control input-lg" name="password" placeholder="Enter your password"
							required="" />
					<i class="fa fa-key form-control-feedback"></i>
				</div>
				<br>

				<button class="btn btn-green btn-flat btn-lg btn-block" type="submit">
					<strong>SIGN UP</strong>
				</button>
			</g:form>
		</div>
	</div>
</div>
</body>
</html>