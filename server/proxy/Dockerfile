FROM nginx
COPY nginx.conf /etc/nginx/nginx.conf

ENV NGINX_CONF="/etc/nginx/nginx.conf"

# Defaults
ENV GENERATOR_URL="http://generator:11770"
ENV AUTOCOMPLETE_URL="http://autocomplete:11880"

# For each environment variable, replace matching $_ or ${_} tokens in the nginx configuration
CMD cat "${NGINX_CONF}" | envsubst "`env | awk -F = '{printf \" $$%s\", $$1}'`" > "${NGINX_CONF}" && nginx -g 'daemon off;'
