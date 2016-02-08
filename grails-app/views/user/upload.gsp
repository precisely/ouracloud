<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title>Upload Users</title>
	<meta name="layout" content="main"/>
	<style>
	@-webkit-keyframes opacity {
		0% { opacity: 1; }
		100% { opacity: 0; }
	}
	@-moz-keyframes opacity {
		0% { opacity: 1; }
		100% { opacity: 0; }
	}

	#loading span {
		-webkit-animation-name: opacity;
		-webkit-animation-duration: 1s;
		-webkit-animation-iteration-count: infinite;

		-moz-animation-name: opacity;
		-moz-animation-duration: 1s;
		-moz-animation-iteration-count: infinite;
	}

	#loading span:nth-child(2) {
		-webkit-animation-delay: 100ms;
		-moz-animation-delay: 100ms;
	}

	#loading span:nth-child(3) {
		-webkit-animation-delay: 300ms;
		-moz-animation-delay: 300ms;
	}

	#loading span:nth-child(4) {
		-webkit-animation-delay: 400ms;
		-moz-animation-delay: 400ms;
	}

	#loading span:nth-child(5) {
		-webkit-animation-delay: 500ms;
		-moz-animation-delay: 500ms;
	}
	</style>
</head>
<body>
	<div class="container page-wrapper">
		<h2 class="page-header">User Import</h2>
		<p id="loading" style="display: none" class="alert alert-info">Importing Users<span>.</span><span>.</span><span>.</span><span>.</span><span>.</span></p>
		<g:if test="${totalRecords}">
			<div class="alert alert-info">
				Import finished:<br/>
				Total records imported: ${totalRecords}<br/>
				Existing users: ${existingUsers}<br/>
				New users: ${totalRecords - existingUsers - failedEmails.size()}<br/>
				Failed Imports: ${failedImport}<br/>
				Failed user creation for emails: ${failedEmails.join(", ")}
			</div>
		</g:if>
		<g:uploadForm action="upload" method="post">
			<div class="form-group">
				<label for="csvInput">Input file</label>
				<input id="csvInput" type="file" name="userFile" accept=".csv">
			</div>

			<input type="submit" class="btn btn-green btn-rounded btn-md">
		</g:uploadForm>
		<asset:script>
			$("input[type=submit]").click(function() {
				$(".alert-info").hide();
				$("#loading").show();
			});
		</asset:script>
	</div>
</body>
</html>