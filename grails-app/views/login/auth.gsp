<html>
<head>
	<meta name="layout" content="ring"/>
	<title>Log In - Ōura Ring</title>
</head>
<body>
	<div class="row df-margin-top">
		<div class="col-sm-8 col-sm-push-2">
			<div class="well-wrapper login-panel">
				<div class="btn-group btn-group-justified">
					<a href="/signup" class="btn btn-block btn-lg btn-flat btn-default">Sign Up</a>
					<a href="/login" class="btn btn-block btn-lg btn-flat btn-green">Log In</a>
				</div>
				<h2 class="title">Welcome Back!</h2>

				<form action="${postUrl}" method="POST">
					<div class="form-group has-feedback feedback-left">
						<input type="text" class="form-control input-lg" name="j_username"
							placeholder="Enter your username" required autofocus />
						<i class="fa fa-user form-control-feedback"></i>
					</div>

					<div class="form-group has-feedback feedback-left">
						<input type="password" class="form-control input-lg" name="j_password"
							placeholder="Enter your password" required />
						<i class="fa fa-key form-control-feedback"></i>
					</div>

					<div class="row text-muted forgot-container">
						<div class="col-sm-6">
							<div class="checkbox">
								<label>
									<input type="checkbox" class="chk" name="${rememberMeParameter}" id="remember_me" <g:if test="${hasCookie}">checked="checked"</g:if>/>
									Remember me
								</label>
							</div>
						</div>
						<div class="col-sm-6 text-right" style="margin-top: 10px;">
							<a href="/forgot-password">Forgot your password?</a>
						</div>
					</div>
					<br>

					<button class="btn btn-green btn-flat btn-lg btn-block" type="submit">
						<strong>LOG IN TO ŌURA</strong>
					</button>
				</form>
			</div>
		</div>
	</div>
</body>
</html>