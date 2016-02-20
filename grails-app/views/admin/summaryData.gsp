<html>
<head>
	<meta name="layout" content="main">
	<title>Summary Data</title>
</head>
<body>
	<div class="container">
		<h1 class="page-header">
			Summary Data
		</h1>

		<div class="row">
			<g:form action="summaryData" name="search" method="get">
				<div class="col-sm-3">
					<g:textField name="query" value="${params.query}" autofocus="" class="form-control"
						 placeholder="Search by email, user id" />
				</div>
				<div class="col-sm-3">
					<select name="dataType" class="form-control">
						<option value="">Select Type</option>
						<g:each in="${us.wearecurio.model.SummaryDataType.values()}" var="type">
							<option value="${type.name()}"
								${params.dataType == type.name() ? "selected" : ""}>${type.name()}</option>
						</g:each>
					</select>
				</div>
				<div class="col-sm-2">
					<g:field type="date" name="startDate" value="${params.startDate}" class="form-control"
						 placeholder="MM/dd/yyyy" />
				</div>
				<div class="col-sm-2">
					<g:field type="date" name="endDate" value="${params.endDate}" class="form-control"
						 placeholder="MM/dd/yyyy" />
				</div>
				<div class="col-sm-2">
					<button type="submit" class="btn btn-primary"><i class="fa fa-search"></i></button>
				</div>
			</g:form>
		</div>
		<br>

		<table class="table table-bordered table-hover">
			<thead>
				<tr>
					<th>User</th>
					<g:sortableColumn property="eventTime" title="Event Time" />
					<g:sortableColumn property="type" title="Data Type" />
					<g:sortableColumn property="timeZone" title="Time Zone" />
					<th>Data</th>
					<g:sortableColumn property="dateCreated" title="Record Created" />
				</tr>
			</thead>
			<tbody>
				<g:each in="${summaryDataInstanceList}" var="summaryDataInstance">
					<tr>
						<td>
							${summaryDataInstance.user.email}
						</td>
						<td>
							<g:formatDate date="${new Date((summaryDataInstance.eventTime * 1000))}"
								format="yyyy-MM-dd hh:mm:ss a" />
						</td>
						<td>
							${summaryDataInstance.type.name()}
						</td>
						<td>
							${summaryDataInstance.timeZone}
						</td>
						<td>
							<ul class="list-inline">
								<g:each in="${summaryDataInstance.data}" var="data">
									<li>
										<span style="font-weight: bold;">${data.key}</span>: ${data.value}
									</li>
								</g:each>
							</ul>
						</td>
						<td>
							<g:formatDate date="${summaryDataInstance.dateCreated}" format="yyyy-MM-dd hh:mm:ss a" />
						</td>
					</tr>
				</g:each>
			</tbody>
			<tfoot>
				<tr>
					<th colspan="7">
						<g:set var="limit" value="${ params.offset.toInteger() + (params.max ?: 10).toInteger()}" />
						<small>
							Showing: <strong>${params.offset.toInteger() + 1}-${limit > summaryDataInstanceTotal ? summaryDataInstanceTotal : limit}</strong>
							of <strong>${summaryDataInstanceTotal}</strong>
						</small>
					</th>
				</tr>
			</tfoot>
		</table>

		<div class="pagination">
			<g:paginate total="${summaryDataInstanceTotal}" params="${params}" />
		</div>
	</div>

	<script>
		<g:each in="${summaryDataInstanceList}" var="summaryDataInstance" status="index">
			console.log("${index + 1}. Raw: ${summaryDataInstance.eventTime}. new Date(${summaryDataInstance.eventTime} * 1000) = ", new Date(${summaryDataInstance.eventTime} * 1000));
		</g:each>
	</script>

	<asset:script>
		$("a", ".pagination").wrap("<li></li>");
		$(".step.gap", ".pagination").wrap("<li></li>");
		$(".currentStep", ".pagination").wrap("<li class='active'></li>")
		$(".pagination").wrapInner("<ul class='pagination'></ul>");

		$("th.sortable.sorted").each(function() {
			if ($(this).hasClass("asc"))
				$(this).append(" &nbsp;<i class=\"fa text-info fa-caret-up\"></i>");
			else
				$(this).append(' &nbsp;<i class="fa text-info fa-caret-down"></i>');
		});
	</asset:script>
</body>
</html>