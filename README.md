图书馆助手app的服务器
口号是：做更懂你的图书馆助手

先是要爬数据下来
再者是要到图书馆同步内网数据

docker pull elasticsearch

docker volume create portainer_data

docker run -d -p 8000:8000 -p 9000:9000 --name=portainer --restart=always -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer-ce
