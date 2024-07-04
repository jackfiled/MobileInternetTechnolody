FROM mcr.microsoft.com/dotnet/aspnet:8.0

RUN sed -i "s?http://deb.debian.org?https://mirrors.cernet.edu.cn?g" /etc/apt/sources.list.d/debian.sources
RUN apt update && apt upgrade -y && apt install -y ffmpeg

WORKDIR /App
COPY bin/Release/net8.0/publish /App
ENTRYPOINT ["dotnet", "/App/Chiara.dll"]
