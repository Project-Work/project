def get_bearer_token(c, a):
    import base64
    import requests
    cred = '{}:{}'.format(c,a)
    cred_enc = base64.b64encode(cred.encode())
    resp = requests.post(
        'https://api.twitter.com/oauth2/token',
        headers={'Authorization': 'Basic {}'.format(cred_enc.decode())},
        data={'grant_type' : 'client_credentials'}
    )
    if resp.status_code == 200:
        resp_data = resp.json()
        return resp_data['access_token']
    else:
        raise ValueError(
           'errore nella response, status {}, text {}'.format(resp.status_code, resp.text
            ))

def get_tweet():
    import psycopg2
    import requests
    import time
    token = get_bearer_token('eI73Ec9V7eBBctSMTQ4CmiYm6', 'KcrqXsaxFNXbxtqUWh1FROxE3tZUQhKegvZKiKj3jO42j4gh2r')
    conn = psycopg2.connect(user = 'ecommerce', password= 'password' , dbname= 'projectwork', host= '192.168.101.108' , port = 5432 )
    curs = conn.cursor()
    curs.execute('SELECT * FROM countries')
    ids = curs.fetchall()
    url= 'https://api.twitter.com/1.1/search/tweets.json'
    time = time.strftime('%Y-%m-%d')
    i = 0
    while (i < len(ids)):
        resp = requests.get(url, 
            params={'q': 'place:' + ids[i][0] + ' ABAP OR #cpp OR cSharp OR objective-pascal OR Fsharp OR java OR #MATLAB OR objective-c OR pascal OR perl OR #php OR javascript OR visualbasic OR pl/sql OR python OR transact-SQL OR ruby since:2015-05-15', 'count':100}, 
            headers={'Authorization': 'Bearer {}'.format(token)}
            )
        data = resp.json()
        IDstate = ids[i][0]
        for d in data['statuses']:
            text = d['text']
            date = d['created_at']
            IDtweet = d['id_str']
            curs.execute('INSERT INTO trash_tweet VALUES (%s, %s, %s, %s)',
                (IDtweet, IDstate, date,text )        
            )
        i += 1
    conn.commit()
    curs.close()
    conn.close()

