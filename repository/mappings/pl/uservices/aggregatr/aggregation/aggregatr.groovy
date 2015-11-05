io.codearte.accurest.dsl.GroovyDsl.make {
	request {
		method "POST"
		url "/order"
		body('''
                    {
                    "ingredients":[ "HOP", "WATER", "YEAST", "MALT"]
                    }
                '''
		)
		headers { header("Content-Type", "application/json") }
	}
	response {
		status 200
		body( """{
                        "stock": [
                        {"HOP": 100},
                        {"WATER": 100},
                        {"YEAST": 100},
                        {"MALT": 100}
                        ]
                    }""")
		headers { header('Content-Type': 'application/json') }
	}
}
