<%@ page import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException" %>
<html>
<head>
	<meta name='layout' content='main'/>
	<title>Confirm Access - Ōura Ring</title>
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-sm-6 col-sm-push-3 col-lg-4 col-lg-push-4" style="margin-top: 100px;">
				<g:if test="${lastException && !(lastException instanceof UnapprovedClientAuthenticationException)}">
					<div class="alert alert-danger">
						<strong>Access could not be granted. (${lastException?.message})</strong>
					</div>
				</g:if>
				<g:else>
					<g:if test="${flash.message}">
						<div class="alert alert-danger">${flash.message}</div>
					</g:if>

					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">Please Confirm</h3>
						</div>
						<div class="panel-body">
							<p>
								You hereby authorize <b>${applicationContext.getBean('clientDetailsService')?.loadClientByClientId(params.client_id)?.clientId ?: 'n/a'}</b>
								to access your protected resources.
							</p>
							<br>

							<div class="row">
								<form method="POST" class="col-sm-6">
									<input name="user_oauth_approval" type="hidden" value="true"/>
									<input name="authorize" value="Authorize" type="submit"
									   class="btn btn-block btn-lg btn-primary" />
								</form>
								<form method="POST" class="col-sm-6">
									<input name="user_oauth_approval" type="hidden" value="false"/>
									<input name="deny" value="Deny" type="submit"
									   class="btn btn-block btn-lg btn-default" />
								</form>
							</div>
						</div>
					</div>
				</g:else>
			</div>
		</div>
	</div>
</body>
</html>