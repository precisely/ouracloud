<html>
<head>
	<meta name="layout" content="main"/>
	<title>Login - Ōura Ring</title>
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-sm-6 col-sm-push-3 col-lg-4 col-lg-push-4" style="margin-top: 100px;">
				<g:if test="${flash.message}">
					<div class="alert alert-danger">${flash.message}</div>
				</g:if>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">Login to Ōura</h3>
					</div>
					<div class="panel-body">
						<form action="${postUrl}" method="POST">
							<div class="form-group">
								<label for="email">Email</label>
								<div class="input-group">
									<span class="input-group-addon">
										<i class="fa fa-envelope-o"></i>
									</span>
									<input type="text" class="form-control" name="j_username" id="email" autofocus
										placeholder="Enter your email" required />
								</div>
							</div>

							<div class="form-group">
								<label for="password">Password</label>
								<div class="input-group">
									<span class="input-group-addon">
										<i class="fa fa-key"></i>
									</span>
									<input type="password" class="form-control" name="j_password" id="password"
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

							<button class="btn btn-info btn-lg btn-block" type="submit">Login</button>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
