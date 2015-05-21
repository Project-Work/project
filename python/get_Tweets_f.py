def get_bearer_token(key, key_secret):
    import base64
    import requests
    cred = '{}:{}'.format(key,key_secret)
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

def string_languages():
    import psycopg2
    conn = psycopg2.connect(user = 'projectwork', password= 'projectwork' , dbname= 'projectwork', host= '192.168.101.208' , port = 5432)
    curs = conn.cursor()
    curs.execute('SELECT * FROM languages')
    lang = curs.fetchall()
    l = []
    for p in lang:
        l.append('"' + p[1] + '"')
    l = ' OR '.join(l)
    return l

def convert_date(str_date):
    import datetime
    year = str_date[26:]
    str_date = str_date[:11] + year
    str_date = str_date[4:]
    date = datetime.datetime.strptime(str_date, '%b %d %Y')
    date = date.date()
    return date

def get_tweet(token):
    import psycopg2
    import requests
    import time
    conn = psycopg2.connect(user = 'projectwork', password= 'projectwork' , dbname= 'projectwork', host= '192.168.101.208' , port = 5432)
    curs = conn.cursor()
    curs.execute('SELECT * FROM state')
    ids = curs.fetchall()
    url= 'https://api.twitter.com/1.1/search/tweets.json'
    time = time.strftime('%Y-%m-%d')
    str_lang = string_languages()
    i = 0
    while (i < len(ids)):
        resp = requests.get(url, 
            params={'q':'place:' + ids[i][0] + ' ' + str_lang + 'since:'+ time +', 'count':100}, 
            headers={'Authorization': 'Bearer {}'.format(token)}
            )
        data = resp.json()
        IDstate = ids[i][0]
        for d in data['statuses']:
            text = d['text']
            date = convert_date(d['created_at'])
            IDtweet = d['id_str']
            curs.execute('INSERT INTO dirty_tweets VALUES (%s, %s, %s, %s)',
                (text,IDstate, date, IDtweet)        
            )
        i += 1

    conn.commit()
    curs.close()
    conn.close()


if __name__ == '__main__':
    import os 
    import sys
    token = get_bearer_token(os.environ['APP_KEY'], os.environ['APP_SECRET'])
    get_tweet(token)
    print ("Correct Save")
