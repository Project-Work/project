# project
The coolest programming language
By collecting data from twitter users, you require to infer the trending programming languages in the European states on the last two year.

A similar project, for USA states, can be found here http://mashable.com/2015/01/06/maps-programming-languages-states/

The project can be split up in the following phases:

Collect data from twitter
Data cleanup and analysis
Presentation
Optional: Deploy on Microsoft Azure or AWS
Collect data from twitter

An http client using the twitter APIs, pulls all tweets on the last two years for a specific geographical region containing specific text associated with a set of programming languages.

Raw data is stored in a local database for further analysis.

Data cleanup and analysis

Clean up and analyze the data from the raw data collected.

Data Clean up

Analyze and implement some methodologies for clean up the data and removing unrelated tweets.

For example check the hashtag and the social graph of the user.

Data analysis

Grouping the tweets by months of the year/state of the last 2 years implement the following analysis:

Calculate the rate per month of any language considered and normalize the counted tweets, so that all languages together have a score of 100%.
Determine the rate change of any language considered from the previous month.
Determine the language with the greatest rate and the gratest rate change grouped by year/state.
Optional: Analyze and implement some methodologies to weigh the tweet. For example use the number of followers of the user.

Presentation

Build a webapp to display the results obtained from the data analysis.

Optional: display the results as a chart.

Optional: Deploy on Microsoft Azure or AWS

Deploy the applications on Microsoft Azure or AWS.
