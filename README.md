
# AWS Elasticache iam authentication sample implementation

This project intends to demo the AWS IAM based authentication on Elasticache standalone replication group.



## Acknowledgements

- [Simplify managing access to Amazon ElastiCache - AWS Blog](https://aws.amazon.com/blogs/database/simplify-managing-access-to-amazon-elasticache-for-redis-clusters-with-iam/)
- [Role-Based Access Control (RBAC) - AWS documentation](https://docs.aws.amazon.com/AmazonElastiCache/latest/red-ug/Clusters.RBAC.html)
- [Elasticache iam auth demo app - AWS samples](https://github.com/aws-samples/elasticache-iam-auth-demo-app)


## API Reference

#### Insert advertiser configuration

```http
POST /advertiser
```

| Parameter | Type | Description |
| :-------- | :------- | :------------------------- |
| `advertiserId` | `string` | **Required**. Advertiser ID |
| `advertiserName` | `string` | Advertiser Name |
| `landingPageUrl` | `string` | Home page url |



#### Get advertiser configuration

```http
GET /advertiser/${id}
```

| Parameter | Type | Description |
| :-------- | :------- | :-------------------------------- |
| `id` | `string` | **Required**. ID of the advertiser |


#### Delete advertiser configuration

```http
DELETE /advertiser/${id}
```

| Parameter | Type | Description |
| :-------- | :------- | :-------------------------------- |
| `id` | `string` | **Required**. ID of the advertiser |



## Demo
![alt-text](img/Redis_Demo.gif)

## Environment Variables

To run this project, you will need to configure the AWS credentials.
Since this implementation uses AWS DefaultCredentialsProviderChain, you can decide which ever credentials option you are confortable with. For simplicity we can set the following two values as Environment Variables to configure the AWS sdk authentication.

`AWS_ACCESS_KEY_ID`

`AWS_SECRET_ACCESS_KEY`


