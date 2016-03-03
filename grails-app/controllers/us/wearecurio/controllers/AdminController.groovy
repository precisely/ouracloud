package us.wearecurio.controllers

import grails.plugin.springsecurity.annotation.Secured
import us.wearecurio.model.SummaryData
import us.wearecurio.model.SummaryDataType
import us.wearecurio.users.User

@Secured(["ROLE_ADMIN"])
class AdminController {

	def dashboard() {}

	def summaryData(Integer max, Integer offset, String dataType, String query) {
		params.max = Math.min(max ?: 10, 1000)
		params.offset = offset ?: 0

		User userInstance

		if (query) {
			if (query.isNumber()) {
				userInstance = User.get(query)
			} else {
				userInstance = User.findByEmailIlike(query)
			}
		}

		List<SummaryData> summaryDataInstanceList = SummaryData.createCriteria().list(params) {
			if (query) {
				eq("user", userInstance)
			}

			if (dataType) {
				eq("type", SummaryDataType.lookup(dataType))
			}
			if (params.startDate) {
				gte("eventTime", (long)(params.date("startDate").time / 1000))
			}
			if (params.endDate) {
				lt("eventTime", (long)((params.date("endDate") + 1).clearTime().time / 1000))
			}
		}

		[summaryDataInstanceList: summaryDataInstanceList, summaryDataInstanceTotal: summaryDataInstanceList.totalCount]
	}
}