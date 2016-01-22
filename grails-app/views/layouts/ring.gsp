<g:applyLayout name="main">
	<html>
	<head>
		<meta name="layout" content="main"/>
		<title><g:layoutTitle /></title>
	</head>
	<body class="${pageProperty(name: 'body.class') }">
	<div class="ring-hand">
		<div class="container">
			<div class="row">
				<div class="col-sm-8 col-sm-push-2 col-md-7 col-md-push-5 col-lg-6 col-lg-push-6">
					<g:if test="${!isSignupPage || (isSignupPage && displaySignupForm)}">
						<h2 class="text-center headline">
							Use ŌURA Cloud to share your data with other services, such as We Are Curious.
						</h2>
						<sec:ifNotLoggedIn>
							<h3 class="text-center sub-heading df-gutter-horizontal">
								<g:if test="${session.isOAuth2Authorization}">
									Log in to your ŌURA Cloud account to authorize sharing your data.
								</g:if>
								<g:else>
									Sign in with your ŌURA Store account or create a new account. Afterwards, download the
									<a href="https://itunes.apple.com/us/app/we-are-curious/id1063805457?mt=8">We Are Curious</a>
									app, sign up for an account, and sync your ŌURA Cloud account in your user profile.
								</g:else>
							</h3>
						</sec:ifNotLoggedIn>
					</g:if>
					<g:layoutBody/>
				</div>
			</div>
		</div>
	</div>
	</body>
	</html>
</g:applyLayout>