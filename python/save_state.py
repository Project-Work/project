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


def save_state(state):
    import os
    import psycopg2
    import requests
    token = get_bearer_token(os.environ['APP_KEY'], os.environ['APP_SECRET'])
    url = 'https://api.twitter.com/1.1/geo/search.json'
    resp = requests.get(
        url,
        params={'granularity':'country', 'query': state},
        headers={'Authorization': 'Bearer {}'.format(token)}
        )
    data = resp.json()
    id = data['result']['places'][0]['id']
    conn = psycopg2.connect(user = 'projectwork', password= 'password' , dbname= 'projectwork', host= '127.0.0.1' , port = 5432 )
    curs = conn.cursor()
    curs.execute('INSERT INTO countries VALUES (%s, %s)',
        (id, state)
    )
    conn.commit()
    curs.close()
    conn.close()
