<html>
<head>
	<meta name="layout" content="ring"/>
	<title>Sign Up - Ōura Ring</title>
</head>
<body class="signup-page">
	<g:if test="${displaySignupForm}">
		<content tag="messages">
			<h2 class="text-center headline">
				ŌURA Cloud lets you synchronize your ring data with services such as <oura:iTuneAppLink/> to give you
				even greater insight into your sleep and health.
			</h2>
			<h3 class="text-center sub-heading df-gutter-horizontal">
				To begin syncing with the cloud, sign in with your ŌURA Store account or create a new account.
			</h3>
		</content>

		<div class="row">
			<div class="col-md-8 col-md-push-2">
				<div class="well-wrapper login-panel">
					<div class="btn-group btn-group-justified">
						<a href="/signup?beta=${params.beta}&ouraapp=${params.ouraapp}"
							class="btn btn-block btn-lg btn-rounded btn-green">Sign Up</a>
						<a href="/login" class="btn btn-block btn-lg btn-rounded btn-default">Sign In</a>
					</div>
					<h2 class="title">Get Started</h2>

					<g:form action="signup" method="POST">
						<g:hiddenField name="beta" value="${params.beta}" />
						<g:hiddenField name="ouraapp" value="${params.ouraapp}" />
						<div class="form-group has-feedback feedback-left ${hasErrors(bean: userInstance, field: 'email',
							'has-error')}">
							<g:field type="email" class="form-control input-lg" name="email" placeholder="Enter your email"
								value="${userInstance?.email}" required="" autofocus="" />
							<i class="fa fa-envelope-o form-control-feedback"></i>

							<g:hasErrors bean="${userInstance}" field="email">
								<span class="help-block"><g:fieldError field="email" bean="${userInstance}" /></span>
							</g:hasErrors>
						</div>

						<div class="form-group has-feedback feedback-left">
							<g:passwordField class="form-control input-lg" name="password" placeholder="Enter your password"
								required="" value="${params.password}" />
							<i class="fa fa-key form-control-feedback"></i>
						</div>
						<br>

						<button class="btn btn-green btn-rounded btn-lg btn-block" type="submit">
							<strong>Create ŌURA Cloud Account</strong>
						</button>
					</g:form>
				</div>
			</div>
		</div>
	</g:if>
	<g:else>
		<h2 class="text-center headline coming-soon">
			COMING SOON: ŌURA Cloud will let you share your data with other services, such as
			<a href="https://www.wearecurio.us/">We Are Curious</a>.
		</h2>
	</g:else>
</body>
</html>