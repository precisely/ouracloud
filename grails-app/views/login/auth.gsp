<html>
<head>
	<meta name="layout" content="ring"/>
	<title>Login - Ōura Ring</title>
</head>
<body>
	<div class="row df-margin-top">
		<div class="col-sm-8 col-sm-push-2">
			<g:if test="${flash.message}">
				<div class="alert alert-danger">${flash.message}</div>
			</g:if>

			<form action="${postUrl}" method="POST">
				<label>Email</label>
				<div class="form-group">
					<div class="input-group">
						<span class="input-group-addon">
							<i class="fa fa-envelope-o"></i>
						</span>
						<input type="text" class="form-control input-lg" name="j_username" id="email"
							placeholder="Enter your email" required autofocus />
					</div>
				</div><br>

				<div class="form-group">
					<label>Password</label>
					<div class="input-group">
						<span class="input-group-addon">
							<i class="fa fa-key"></i>
						</span>
						<input type="password" class="form-control input-lg" name="j_password" id="password"
							placeholder="Enter your password" required />
					</div>
				</div>

				<div class="checkbox">
					<label>
						<input type="checkbox" class="chk" name="${rememberMeParameter}" id="remember_me" <g:if test="${hasCookie}">checked="checked"</g:if>/>
						Remember me
					</label>
				</div>
				<br>

				<div class="text-center df-margin-top">
					<button class="btn btn-success btn-green btn-lg btn-block" type="submit">
						Login to Ōura
					</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>