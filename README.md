图书馆助手app的服务器
口号是：做更懂你的图书馆助手

先是要爬数据下来
再者是要到图书馆同步内网数据

docker pull elasticsearch

docker volume create portainer_data

docker run -d -p 8000:8000 -p 9000:9000 --name=portainer --restart=always -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer-ce


#MySQL服务器需要改字符集为utf8mb4：

```text
[client]
port=3306
default-character-set=utf8mb4

[mysql]

default-character-set=utf8mb4


# SERVER SECTION
# ----------------------------------------------------------------------
#
# The following options will be read by the MySQL Server. Make sure that
# you have installed the server correctly (see above) so it reads this 
# file.
#
[mysqld]

# The TCP/IP Port the MySQL Server will listen on
port=3306


#Path to installation directory. All paths are usually resolved relative to this.
basedir="C:/Program Files/MySQL/MySQL Server 5.5/"

#Path to the database root
datadir="C:/ProgramData/MySQL/MySQL Server 5.5/Data/"

# The default character set that will be used when a new schema or table is
# created and no character set is defined
character-set-server=utf8mb4

character-set-client-handshake=FALSE
collation-server=utf8mb4_unicode_ci
init_connect='SET NAMES utf8mb4'
```