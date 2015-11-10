io.codearte.accurest.dsl.GroovyDsl.make {
    request {
        method 'POST'
        url '/ingredients'
        headers {
            header 'Content-Type': 'application/vnd.pl.uservices.aggregatr.v1+json'
        }
        body('''
            { "items" : ["YEAST"] }
        ''')
    }
    response {
        status 200
        body(
            ingredients: [
                    [type: 'YEAST', quantity: 200]
            ]
        )
    }
}