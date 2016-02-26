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
						<g:if test="${!pageProperty(name: 'page.messages')}">
							<g:if test="${!isSignupPage || (isSignupPage && displaySignupForm)}">
								<h2 class="text-center headline">
									Store and synchronize your ring data with other services like
									<span class="nowrap">We Are Curious</span> using ŌURA Cloud.
								</h2>
								<sec:ifNotLoggedIn>
									<h3 class="text-center sub-heading df-gutter-horizontal">
										<g:if test="${session.isOAuth2Authorization}">
											Sign in to your ŌURA Cloud account to authorize sharing your data.
										</g:if>
										<g:else>
											Sign in or create a new account, then download the <oura:iTuneAppLink/>
											app and link with ŌURA through your Curious user profile.
										</g:else>
									</h3>
								</sec:ifNotLoggedIn>
							</g:if>
						</g:if>
						<g:else>
							<g:pageProperty name="page.messages"/>
						</g:else>
						<g:layoutBody/>
					</div>
				</div>
			</div>
		</div>
	</body>
	</html>
</g:applyLayout>