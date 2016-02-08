<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title>Upload Users</title>
	<meta name="layout" content="main"/>
</head>
<body>
	<div class="container page-wrapper">
		<h2 class="page-header">User Import</h2>
		<p id="loading" style="display: none" class="alert alert-info">
			<i class="fa fa-fw fa-spin fa-spinner"></i>
			Importing Users
		</p>
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