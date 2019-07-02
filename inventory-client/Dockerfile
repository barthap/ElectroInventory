FROM node:8-alpine as client

# Create app directory
WORKDIR /usr/app/client/
# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
# where available (npm@5+)
COPY package*.json ./

#RUN npm install
# If you are building your code for production, use
RUN npm ci --only=production

# Bundle app source
COPY . ./
RUN npm run build

# STAGE 2
FROM node:8-alpine

WORKDIR /usr/app/
COPY --from=client /usr/app/client/build/  ./build/

WORKDIR /usr/app/server/
COPY server/package*.json ./
RUN npm install -qy
COPY server/* /usr/app/server/

ARG EXPOSE_PORT=4123

ENV PORT ${EXPOSE_PORT}}
EXPOSE ${EXPOSE_PORT}

CMD [ "npm", "start" ]