class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?(.$format)?" {
			constraints {
				// apply constraints here
			}
		}

		"/api/sync" {
			controller = "data"
			action = "sync"
		}

		"/api/$dataType/$id?" {
			controller = "data"
			action = {
				Map actionMethodMap = [GET: params.id ? "get" : "index", POST: "save", PUT: "update", DELETE: "delete"]

				return actionMethodMap[request.method.toUpperCase()]
			}
			constraints {
				dataType inList: ["all", "activity", "exercise", "sleep"]
			}
		}

		"/"(view: "/index")
		"500"(view: '/error')
	}
}
