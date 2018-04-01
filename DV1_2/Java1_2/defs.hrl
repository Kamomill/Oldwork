% This record defines the structure of the client process.
% Add whatever other fields you need.
% It contains the following fields:
%   gui: the name (or Pid) of the GUI process.
-record(client_st, {gui, nick ,serverstate ,server , channelList}). %added channel, the client should know whitch channels it's connected to

% This record defines the structure of the server process.
% Add whatever other fields you need.
-record(server_st, {serverName, channelList, nickList}). %added channelList, a list of channels that is avaible to use
-record(channel_st,{channelName, serverName, nickPidList}). %!
