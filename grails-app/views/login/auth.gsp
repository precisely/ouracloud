<html>
<head>
	<meta name="layout" content="ring"/>
	<title>Log In - Ōura Ring</title>
</head>
<body class="login-page">
	<div class="row">
		<div class="col-md-8 col-md-push-2">
			<div class="well-wrapper login-panel">
				<g:if test="${!session.isOAuth2Authorization}">
					<div class="btn-group btn-group-justified">
						<a href="/signup" class="btn btn-block btn-lg btn-default">Sign Up</a>
						<a href="/login" class="btn btn-block btn-lg btn-green">Log In</a>
					</div>
				</g:if>
				<h2 class="title">Welcome Back!</h2>

				<form action="${postUrl}" method="POST">
					<div class="form-group has-feedback feedback-left">
						<input type="text" class="form-control input-lg" name="j_username"
							placeholder="Enter your email" required autofocus />
						<i class="fa fa-user form-control-feedback"></i>
					</div>

					<div class="form-group has-feedback feedback-left">
						<input type="password" class="form-control input-lg" name="j_password"
							placeholder="Enter your password" required />
						<i class="fa fa-key form-control-feedback"></i>
					</div>

					<div class="row text-muted forgot-container">
						<div class="col-xs-6">
							<div class="checkbox">
								<label>
									<input type="checkbox" class="chk" name="${rememberMeParameter}" id="remember_me" <g:if test="${hasCookie}">checked="checked"</g:if>/>
									Remember me
								</label>
							</div>
						</div>
						<div class="col-xs-6 text-right" style="margin-top: 10px;">
							<a href="/forgot-password">Forgot <span class="hidden-xs">your</span> password?</a>
						</div>
					</div>
					<br>

					<button class="btn btn-green btn-rounded btn-lg btn-block" type="submit">
						<strong>LOG IN TO ŌURA</strong>
					</button>
				</form>
			</div>
		</div>
	</div>
</body>
</html>