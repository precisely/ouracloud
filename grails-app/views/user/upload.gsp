<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title>Upload Users</title>
	<meta name="layout" content="main"/>
</head>
<body>
	<div class="container page-wrapper">
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
				<input id="csvInput" type="file" name="userFile" accept="text/csv">
			</div>

			<input type="submit" class="btn btn-default">
		</g:uploadForm>
	</div>
</body>
</html>