# deploy-suite

# 要解决的问题
自动部署。

# 看起来应该会是这样
```bash
deploy --type copy \
       --from ./ \
       --to /home/someone \
       --destination someone@localhost \
       --destination-privateKey private_key_base64 \
       --destination-password 
```