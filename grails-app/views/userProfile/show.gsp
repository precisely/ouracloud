<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<meta name="layout" content="main"/>
	<title>My Account - Ōura Ring</title>
</head>
<body>
	<div class="container page-wrapper">
		<div class="row df-margin-top">
			<div class="col-sm-6 col-sm-push-3">
				<div class="panel panel-default">
					<div class="panel-heading"><h3 class="panel-title">Profile</h3></div>
					<div class="panel-body">
						<div class="row">
							<g:form class="col-sm-8" action="" method="POST">
								<div class="form-group ${hasErrors(bean: userInstance, field: 'email', 'has-error')}">
									<label for="email">Email</label>
									<g:field type="text" name="email" value="${userInstance.email}" required=""
										readonly="true" class="form-control" />

									<g:hasErrors bean="${userInstance}" field="email">
										<span class="help-block"><g:fieldError field="email" bean="${userInstance}" /></span>
									</g:hasErrors>
								</div>
							</g:form>
						</div>
					</div>
				</div>

				<div class="panel panel-default">
					<div class="panel-heading"><h3 class="panel-title">Change Password</h3></div>
					<div class="panel-body">
						<div class="row">
							<g:form class="col-sm-8" action="updatePassword" method="POST">
								<div class="form-group">
									<label for="oldPassword">Old Password</label>
									<g:passwordField name="oldPassword" class="form-control" required="" />
								</div>

								<div class="form-group">
									<label for="password">
										New Password
										<i class="fa fa-question-circle fa-fw" data-toggle="tooltip"
										   title="Password must not be same as the email and must be minimum of 6 characters long."></i>
									</label>
									<g:passwordField name="password" class="form-control" required="" />
								</div>
								<asset:script>
									$('[data-toggle="tooltip"]').tooltip();
								</asset:script>

								<div class="form-group">
									<label for="password2">Confirm New Password</label>
									<g:passwordField name="password2" class="form-control" required="" />
								</div>
								<button type="submit" class="btn btn-default">Update Password</button>
							</g:form>
						</div>
					</div>
				</div>

				<div class="panel panel-danger">
					<div class="panel-heading"><h3 class="panel-title">Delete Account</h3></div>
					<div class="panel-body">
						<g:form action="delete" method="POST">
							<p class="text-muted">Once you delete your account, there is no going back. Please be
							certain.</p>
							<button type="submit" class="btn btn-danger"
									onclick="return confirm('Are you sure?')">Delete My Account</button>
						</g:form>
					</div>
				</div>

				<!-- Fix this and actual URL -->
				<a href="OuraApp://?token=${accessToken}" class="btn btn-green btn-lg btn-block btn-rounded visible-xs visible-sm">
					Go to the App
				</a>
				<br>
			</div>
		</div>
	</div>
</body>
</html>