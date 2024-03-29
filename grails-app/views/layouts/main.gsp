<%@ page import="us.wearecurio.utility.Utils" %>
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
		<link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}">
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
						<sec:ifAnyGranted roles="ROLE_CLIENT_MANAGER">
							<li><g:link controller="admin" action="dashboard">Admin Dashboard</g:link></li>
						</sec:ifAnyGranted>
						<sec:ifLoggedIn>
							<li><g:link uri="/my-account">My Account</g:link></li>
							<li>
								<oura:logoutLink />
							</li>
						</sec:ifLoggedIn>
						<sec:ifNotLoggedIn>
							<li><g:link uri="/login">Sign In</g:link></li>
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
					<div class="col-xs-12 col-sm-4 df-margin-bottom visible-md-block visible-lg-block">
						<h1 class="headline-logo">
							<a href="/" title="Ōura">
								<asset:image src="OURA-logo-white.png" />
							</a>
						</h1>
					</div>
					<div class="col-xs-12 col-sm-8 footer-menu">
						<div class="row">
							<div class="col-xs-12 col-sm-4 visible-md-block visible-lg-block">
								<h4 class="margin-z">About</h4>
								—
								<ul class="list-unstyled">
									<li>
										<a href="http://ouraring.com/frequently-asked-questions/" target="_blank">FAQ</a>
									</li>
									<li>
										<a href="http://ouraring.com/press-releases" target="_blank">Press</a>
									</li>
								</ul>
							</div>
							<div class="col-xs-12 col-sm-4">
								<ul class="list-unstyled clearfix">
									<li class="mobile-only">
										<a href="http://ouraring.com/terms-of-use/" target="_blank">Terms of Use</a>
									</li>
									<li class="mobile-only">
										<a href="http://ouraring.com/privacy-policy/" target="_blank">Privacy Policy</a>
									</li>
									<li class="visible-md-block visible-lg-block">
										<a href="http://ouraring.com/returns/" target="_blank">Returns</a>
									</li>
								</ul>
							</div>
							<div class="col-xs-12 col-sm-4 visible-md-block visible-lg-block">
								<h4 class="margin-z">Join Us On</h4>
								—
								<ul class="list-inline social-icons">
									<li>
										<a href="http://www.facebook.com/ouraring">
											<i class="fa fa-facebook-square"></i>
										</a>
									</li>
									<li>
										<a href="http://www.twitter.com/ouraring">
											<i class="fa fa-twitter-square"></i>
										</a>
									</li>
									<li>
										<a href="https://instagram.com/ouraring">
											<i class="fa fa-instagram"></i>
										</a>
									</li>
									<li>
										<a href="https://www.youtube.com/channel/UCf-xFf4xPcT9DVdOkcCaScg">
											<i class="fa fa-youtube-square"></i>
										</a>
									</li>
									<li>
										<a href="http://www.vimeo.com/oura/">
											<i class="fa fa-vimeo-square"></i>
										</a>
									</li>
									<li>
										<a href="https://www.pinterest.com/Ouraring">
											<i class="fa fa-pinterest-square"></i>
										</a>
									</li>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</footer>

		<div class="modal fade" tabindex="-1" role="dialog" id="confirm-logout-modal">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title">Confirm</h4>
					</div>
					<div class="modal-body">
						<p>Logging out will pause syncing of your ring data with the ŌURA Cloud until you sign in
						again. Do you want to log out?</p>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
						<oura:logoutURL>
							<a href="${it}" class="btn btn-primary" id="do-logout"><strong>OK</strong></a>
						</oura:logoutURL>
					</div>
				</div>
			</div>
		</div>

		<asset:javascript src="jquery/jquery-1.11.3.min.js"></asset:javascript>
		<asset:javascript src="jquery/bootstrap.min.js"></asset:javascript>
		<asset:javascript src="jquery/simpler-sidebar-1.4.0.min.js"></asset:javascript>
		<asset:javascript src="base.js"></asset:javascript>
		<asset:deferredScripts/>
		<script>
			$(document).ready(function() {
				$('#confirm-logout-modal').on('shown.bs.modal', function() {
					// Focus the "OK" button so that pressing enter will hit the button (like Javascript "confirm")
					$("#do-logout").focus();
				});
			});
		</script>
	</body>
</html>