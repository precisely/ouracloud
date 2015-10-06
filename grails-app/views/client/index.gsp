<html>
<head>
	<meta name="layout" content="main">
	<g:set var="entityName" value="${message(code: 'client.label')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
	<div class="container">
		<h1 class="page-header">
			Client Apps
		</h1>

		<div class="row">
			<div class="col-lg-4">
				<g:form action="index" name="search" method="get">
					<div class="input-group">
						<g:textField name="query" value="${params.query}" autofocus="" class="form-control input-sm"
							placeholder="Search" />
						<div class="input-group-btn">
							<button type="submit" class="btn btn-sm btn-primary" ><i class="fa fa-search"></i></button>
						</div>
					</div>
				</g:form>
			</div>
			<div class="col-lg-2 pull-right">
				<g:link action="create" class="btn btn-sm btn-success btn-block">
					<i class="fa fa-plus fa-fw"></i> Create New
				</g:link>
			</div>
		</div>
		<br>

		<table class="table table-bordered table-hover table-striped">
			<thead>
				<tr>
					<g:sortableColumn property="clientName" title="Client Name" />
					<g:sortableColumn property="clientId" title="${message(code: 'client.clientId.label', default: 'Client Id')}" />
					<g:sortableColumn property="accessTokenValiditySeconds" title="${message(code: 'client.accessTokenValiditySeconds.label', default: 'Access Token Validity Seconds')}" />
					<g:sortableColumn property="additionalInformation" title="${message(code: 'client.additionalInformation.label', default: 'Additional Information')}" />
					<g:sortableColumn property="refreshTokenValiditySeconds" title="${message(code: 'client.refreshTokenValiditySeconds.label', default: 'Refresh Token Validity Seconds')}" />
					<th>Scope</th>
				</tr>
			</thead>
			<tbody>
				<g:each in="${clientInstanceList}" var="clientInstance">
					<tr>
						<td><g:link action="show" id="${clientInstance.id}">${clientInstance.name}</g:link></td>
						<td>${clientInstance.clientId}</td>
						<td>${fieldValue(bean: clientInstance, field: "accessTokenValiditySeconds")}</td>
						<td>${fieldValue(bean: clientInstance, field: "additionalInformation")}</td>
						<td>${fieldValue(bean: clientInstance, field: "refreshTokenValiditySeconds")}</td>
						<td>${clientInstance.scopes?.join(", ")}</td>
					</tr>
				</g:each>
				<g:if test="${!clientInstanceList }">
					<tr>
						<td colspan="3">
							No records found. <g:link action="create">Create new</g:link>.
						</td>
					</tr>
				</g:if>
			</tbody>
		</table>

		<div class="pagination">
			<g:paginate total="${clientInstanceTotal}" />
		</div>
	</div>
</body>
</html>