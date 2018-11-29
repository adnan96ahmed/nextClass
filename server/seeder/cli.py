#!/usr/bin/python
import click
import json
import extract

DATAPATH = "static-scraped/"

@click.group()
def cli():
	"""
	A bridge from Guelph's WebAdvisor to PostgreSQL.
	"""
	pass

@click.command()
@click.option('--database', default = 'nextclass', help = 'database name')
@click.option('--hostname', default = 'localhost', help = 'hostname of server')
@click.option('--port', default = 5432, help = 'port number')
@click.option('--username', default = 'postgres', help = 'user to connect with')
@click.option('--password', prompt = True, hide_input = True, default = '', help = 'password for user')
@click.option('--schema', default = 'main', help = 'schema to populate')
def seed(database, hostname, port, username, password, schema):
	"""
	Upload scraped data to an SQL DB.
	"""
	click.echo('Preparing to extract data.')
	extract.seed(username, password, hostname, port, database, schema, DATAPATH)

cli.add_command(seed)

if __name__ == "__main__":
	cli()
