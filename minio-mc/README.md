#use

#add
`mc alias set <ALIAS> <YOUR-S3-ENDPOINT> [YOUR-ACCESS-KEY] [YOUR-SECRET-KEY] [--api API-SIGNATURE]`
example:
`mc alias set minio http://minio:9000 2ChppvMTV5HluQwosGnv lHiafRl36e8Mg8hgR9WKxVte7NOWdmG6ZvAuXAuJ --api S3v4`

#list
mc ls minio

#read
mc cat minio/foo.bar

