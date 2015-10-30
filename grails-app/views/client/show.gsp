<html>
<head>
	<meta name="layout" content="main">
	<g:set var="entityName" value="${message(code: 'client.label')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>
<body>
	<div class="container">
		<h1 class="page-header">
			Client
			<small>${clientInstance.name}</small>
		</h1>

		<dl class="dl-horizontal">
			<dt>
				<g:message code="client.clientId.label" default="Client Id" />
			</dt>
			<dd>
				${clientInstance.clientId}
			</dd>

			<g:if test="${clientInstance?.accessTokenValiditySeconds}">
				<dt>
					<g:message code="client.accessTokenValiditySeconds.label" default="Access Token Validity Seconds" />
				</dt>
				<dd>
					<g:fieldValue bean="${clientInstance}" field="accessTokenValiditySeconds"/>
				</dd>
			</g:if>

			<g:if test="${clientInstance?.additionalInformation}">
				<dt>
					<g:message code="client.additionalInformation.label" default="Additional Information" />
				</dt>
				<dd>
					<g:fieldValue bean="${clientInstance}" field="additionalInformation"/>
				</dd>
			</g:if>

			<g:if test="${clientInstance?.authorities}">
				<dt>
					<g:message code="client.authorities.label" default="Authorities" />
				</dt>
				<dd>
					${clientInstance.authorities.join(", ")}
				</dd>
			</g:if>

			<g:if test="${clientInstance?.authorizedGrantTypes}">
				<dt>
					<g:message code="client.authorizedGrantTypes.label" default="Authorized Grant Types" />
				</dt>
				<dd>
					${clientInstance.authorizedGrantTypes.join(", ")}
				</dd>
			</g:if>

			<g:if test="${clientInstance?.autoApproveScopes}">
				<dt>
					<g:message code="client.autoApproveScopes.label" default="Auto Approve Scopes" />
				</dt>
				<dd>
					${clientInstance.autoApproveScopes.join(", ")}
				</dd>
			</g:if>

			<g:if test="${clientInstance?.clientSecret}">
				<dt>
					<g:message code="client.clientSecret.label" default="Client Secret" />
				</dt>
				<dd>
					<g:fieldValue bean="${clientInstance}" field="clientSecret"/>
				</dd>
			</g:if>

			<g:if test="${clientInstance?.redirectUris}">
				<dt>
					<g:message code="client.redirectUris.label" default="Redirect Uris" />
				</dt>
				<dd>
					${clientInstance.redirectUris.join(", ")}
				</dd>
			</g:if>

			<g:if test="${clientInstance?.refreshTokenValiditySeconds}">
				<dt>
					<g:message code="client.refreshTokenValiditySeconds.label" default="Refresh Token Validity Seconds" />
				</dt>
				<dd>
					<g:fieldValue bean="${clientInstance}" field="refreshTokenValiditySeconds"/>
				</dd>
			</g:if>

			<g:if test="${clientInstance?.resourceIds}">
				<dt>
					<g:message code="client.resourceIds.label" default="Resource Ids" />
				</dt>
				<dd>
					${clientInstance.resourceIds.join(", ")}
				</dd>
			</g:if>

			<g:if test="${clientInstance?.scopes}">
				<dt>
					<g:message code="client.scopes.label" default="Scopes" />
				</dt>
				<dd>
					${clientInstance.scopes.join(", ")}
				</dd>
			</g:if>

		</dl>

		<g:form class="well">
			<fieldset class="form-actions">
				<g:hiddenField name="id" value="${clientInstance?.id}" />
				<g:link class="btn btn-primary" action="edit" id="${clientInstance?.id}">
					<g:message code="default.button.edit.label" default="Edit" />
				</g:link>

				<g:actionSubmit class="btn btn-danger" action="delete"
					value="${message(code: 'default.button.delete.label')}"
					onclick="return confirm('${message(code: 'default.button.delete.confirm.message')}');" />
			</fieldset>
		</g:form>
	</div>
</body>
</html>