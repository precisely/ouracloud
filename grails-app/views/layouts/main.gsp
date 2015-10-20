<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Ōura Ring - Personal technology that silently adapts your lifestyle" /></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
  		<asset:stylesheet src="bootstrap.min.css"/>
  		<asset:stylesheet src="layout.css"/>
  		<asset:stylesheet src="defaults.css"/>
  		<asset:stylesheet src="custom.css"/>
		<g:layoutHead/>
	</head>
	<body class="${pageProperty(name: 'body.class') }">
		<nav class="navbar navbar-default navbar-fixed-top main-header">
			<div class="container">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
							data-target="#main-header-collapse" aria-expanded="false">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="/">
						<asset:image src="OURA-logo-black.png" class="brand-logo" />
					</a>
				</div>

				<div class="collapse navbar-collapse" id="main-header-collapse">
					<ul class="nav navbar-nav navbar-right">
						<sec:ifLoggedIn>
							<li><g:link uri="/my-account">My Account</g:link></li>
							<li><g:link uri="/j_spring_security_logout">Log Out</g:link></li>
						</sec:ifLoggedIn>
						<sec:ifNotLoggedIn>
							<li><g:link uri="/login">Log In</g:link></li>
						</sec:ifNotLoggedIn>
					</ul>
				</div>
			</div>
		</nav>

		<g:render template="/layouts/alertMessage"></g:render>
		<g:layoutBody/>

		<footer class="main-footer">
			<div class="container">
				<div class="row">
					<div class="col-xs-12 col-sm-4 df-margin-bottom">
						<h1 class="headline-logo">
							<a href="http://ouraring.com/" title="Ōura">
								<img src="http://ouraring.com/content/themes/evermade-theme/assets/img/OURA-logo-white.svg" >
							</a>
						</h1>
					</div>
					<div class="col-xs-12 col-sm-8 footer-menu">
						<div class="row">
							<div class="col-xs-12 col-sm-4">
								<ul class="list-unstyled">
									<li><a href="/login">Log In</a></li>
									<li><a href="">FAQ</a></li>
									<li><a href="">Press</a></li>
								</ul>
							</div>
							<div class="col-xs-12 col-sm-4">
								<ul class="list-unstyled">
									<li><a href="">Terms of Use</a></li>
									<li><a href="">Privacy Policy</a></li>
								</ul>
							</div>
							<div class="col-xs-12 col-sm-4">
								<h4>Join Us On</h4>
							</div>
						</div>
					</div>
				</div>
			</div>
		</footer>
		<asset:javascript src="jquery/jquery-1.11.3.min.js"></asset:javascript>
		<asset:javascript src="jquery/bootstrap.min.js"></asset:javascript>
		<asset:javascript src="jquery/simpler-sidebar-1.4.0.min.js"></asset:javascript>
		<asset:javascript src="base.js"></asset:javascript>
		<asset:deferredScripts/>
	</body>
</html>
