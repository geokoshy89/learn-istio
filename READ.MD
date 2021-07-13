####Actual code can be found in below url:
Istio by Examples with Java

```
http://saturnism.me/talk/making-microservices-micro-istio/
```
##Below commands can be used for docker maven plugin:

###Clean and build image:

```
mvn clean package
```

###Push image:

```
mvn dockerfile:push
```

####Add istio in gcloud as below:
```
gcloud beta container clusters update CLUSTER_NAME \
    --update-addons=Istio=ENABLED --istio-config=auth=MTLS_PERMISSIVE  --zone <gcloudzone> --project <gcloudprojectname>
```
The istio deployments will happen in istio-system namespace.
We can scale up and down pods by using below commands for some cases where we want to reload images:

```
kubectl scale --replicas=1 deployment <deployment-name>
```
