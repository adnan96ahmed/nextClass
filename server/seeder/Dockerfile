FROM python:3.7

WORKDIR /src/app
COPY . .
RUN pip install -r requirements.txt

ENTRYPOINT ["python", "cli.py"]
