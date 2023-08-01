METRIC_VALUE=$(sed -n '51p' ../vgsshow/build/reports/kover/htmlDebug/index.html)

echo "Script executed"

curl --request POST \
--url https://verygoodsecurity.atlassian.net/gateway/api/compass/v1/metrics \
--user "$COMPASS_USER_EMAIL:$COMPASS_API_KEY" \
--header "Accept: application/json" \
--header "Content-Type: application/json" \
--data "{
  \"metricSourceId\": \"$COMPASS_COMPONENT_URL\",
  \"value\": $METRIC_VALUE | sed 's/.$//',
  \"timestamp\": \"$(date -u +'%Y-%m-%dT%H:%M:%SZ')\"
}"