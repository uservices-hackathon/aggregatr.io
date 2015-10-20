io.codearte.accurest.dsl.GroovyDsl.make {
    request {
        method 'POST'
        url '/ingredients'
        headers {
            header 'Content-Type': 'application/vnd.pl.uservices.aggregatr.v1+json'
        }
        body('''
            { "items" : ["WATER"] }
        ''')
    }
    response {
        status 200
        body(
            ingredients: [
                    [type: 'WATER', quantity: 200]
            ]
        )
    }
}