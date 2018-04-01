-module(server).
-export([handle/2, chandle/2, initial_state/1]).
%-import(lists).
-include_lib("./defs.hrl").

%% inititial_state/2 and handle/2 are used togetger with the genserver module,
%% explained in the lecture about Generic server.

% Produce initial state
initial_state(ServerName) ->
    #server_st{serverName=ServerName, channelList = [], nickList=[]}.

%% ---------------------------------------------------------------------------

%% handle/2 handles requests from clients

%% All requests are processed by handle/2 receiving the request data (and the
%% current state), performing the needed actions, and returning a tuple
%% {reply, Reply, NewState}, where Reply is the reply to be sent to the client
%% and NewState is the new state of the server.

handle(St, Request) ->
io:fwrite("Server received: ~p~n", [Request]),
io:fwrite("Server has: ~p~n", [St#server_st.nickList]),
case Request of
  %Connect to server
	{connect, Nick} -> %!
			 case lists:member(Nick,St#server_st.nickList) of %if the nick is in the server, then another user
			true-> Response = false,{reply, Response, St}; % can't join, so they get a 'false' in response

			_ ->
				NewSt=St#server_st{nickList=[Nick | St#server_st.nickList]},
   			Response = true,
    				{reply, Response, NewSt}
			end;
  %Disconnect from server
  {disconnect, Nick} ->
	NewSt=St#server_st{nickList = lists:delete(Nick,St#server_st.nickList)},
	{reply, "Disconnected", NewSt};

  %Join channel
	{join, {Channel,Nick,PID}}->
  case lists:member(Nick,St#server_st.nickList) of %if user is in server then try join channel
     true ->
        case lists:member(Channel,St#server_st.channelList) of %if channel exist, join the channel
            true ->                    %there can only be one uniqe nick because it's filtered in server
                ChannelAtom=list_to_atom(Channel),
              	Response = genserver:request(ChannelAtom, {join, {Nick,PID} }), %%request to add user to the channels list
                genserver:request(ChannelAtom, {message,{"<SERVER",PID,"*User: ''" ++Nick ++"'' connected to the channel*"}}),
                %%Server would like to send a message to everyone else that a new user joined

                {reply,Response,St};
            _->
              genserver:start(list_to_atom(Channel), %%if it does not exist make it using genserver,
              % the channel need the pid to send messages to clients
              (#channel_st{channelName=Channel, serverName = St#server_st.serverName, nickPidList=[{Nick,PID}] }),
              fun server:chandle/2),
            NewSt=St#server_st{channelList=[Channel | St#server_st.channelList] },
            Response = "Cerated new channel, joined channel",{reply, Response, NewSt}
      end;
        _-> Response = false,{reply, Response, St} %%if memer, then return a false to client

    end;

    %Leave channel
    {leave, {Channel, Nick, PID}} ->
      case lists:member(Nick,St#server_st.nickList) of
          true->
            ChannelAtom=list_to_atom(Channel),
            genserver:request(ChannelAtom, {message,{"<SERVER",PID,"*User: ''"++Nick++"'' left to the channel*"}}),
            %%Server would like to send a message to everyone else that a user leves the channel

            Response = genserver:request(ChannelAtom, {leave,{Nick, PID}}),
    	      {reply, Response, St};

            _ -> Response = false,{reply, Response, St}
    end;
    %The greeting
    {hello}->{reply, hello, St} %A greeting message of the server, if client does not reccive "hello" it will
                                %send an error of not reaching the server
	end.

chandle(CSt, Request)->
  io:fwrite("Channel received: ~p~n", [Request]),
  case Request of
    %Join chennel
    {join, {Nick, PID}}->
      CNewSt=CSt#channel_st{nickPidList=[{Nick,PID} | CSt#channel_st.nickPidList]},
      {reply, "Joined channel", CNewSt};
    %Leave channel
    {leave, {Nick,PID}}->
      CNewSt=CSt#channel_st{nickPidList=lists:delete({Nick,PID},CSt#channel_st.nickPidList)},
      {reply, "Left cheannel", CNewSt};
    %Message handeling on channel
    {message,{Nick, PID ,Msg }}->
     %we need to send a msg to all but ourself, and we don't want so send one to ourself
    ListOfRecivers= lists:map(fun ({_, Pid}) -> Pid end  ,  CSt#channel_st.nickPidList),% we need a list of pids
    ListOfRecivers2=lists:delete(PID, ListOfRecivers), %list with out the senders message
    lists:foreach( %Spawn a function for each connected users message, increases concurrency
    fun (Pid) -> spawn( fun()-> genserver:request(Pid, {incoming_msg, CSt#channel_st.channelName, Nick, Msg }) end)
  end, ListOfRecivers2),
    {reply, ok, CSt}
end.
