#!/bin/bash

docker run -it --rm \
       -v "$(pwd)":/emacsDocuments \
       -v ~/HandsonMicroservicesWithSpringBootAndSpringCloud/lsp-conf/lsp-session-v1:/home/docker/.emacs.d/.lsp-session-v1 \
       -v ~/HandsonMicroservicesWithSpringBootAndSpringCloud/lsp-conf/treemacs-persist:/home/docker/.emacs.d/.cache/treemacs-persist \
       mulenatic/emacs-docker-java
