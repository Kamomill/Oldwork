-module(client).
-export([handle/2, initial_state/2]).
-include_lib("./defs.hrl").

%% inititial_state/2 and handle/2 are used togetger with the genserver module,
%% explained in the lecture about Generic server.

%% Produce initial state
initial_state(Nick, GUIName) ->
    #client_st { gui = GUIName , nick= Nick , serverstate= unconnected ,channelList = []}.
	%->{client_st, "gui_205839", "yolo", unconnected, SERVER}

%% ---------------------------------------------------------------------------
%% cd("C:/Users/Alex/Desktop/ErlangCode/Lab4_code").
%% cd("C:/Users/Deltagare/Desktop/Erlang_code/Lab4_code").
%% cover:compile_directory().
%% ---------------------------------------------------------------------------
%% handle/2 handles each kind of request from GUI

%% All requests are processed by handle/2 receiving the request data (and the
%% current state), performing the needed actions, and returning a tuple
%% {reply, Reply, NewState}, where Reply is the reply to be sent to the
%% requesting process and NewState is the new state of the client.


%%[Done]
%% Connect to server
handle(St, {connect, Server}) ->
	case St of
	#client_st{serverstate = unconnected}->
ServerAtom = list_to_atom(Server),
    case catch genserver:request(ServerAtom, {hello}) of %greeting message
        hello -> %anything else than 'hello' retunrs error server_not_reached

        Response = genserver:request(ServerAtom, {connect,  St#client_st.nick}),
		    case Response of
		        true-> NewSt = St#client_st{serverstate= connected, server = Server},
 		        {reply, ok, NewSt};

		     _ ->	{reply,{error, user_already_conected, "Nick not avaviable"}, St}
		end;

    _	-> {reply,{error, server_not_reached, "Can not reach server"},St}
  end;

	#client_st{serverstate = connected} -> {reply,{error, user_already_conected, "User already connected to a server"},St}
end;

%% Disconnect from server
handle(St, disconnect) ->
    case St of
    #client_st{serverstate = connected}-> %first we need to if the user is connected to a server
        ServerAtom = list_to_atom(St#client_st.server),
      case catch genserver:request(ServerAtom, {hello}) of %greeting to the server
        hello ->
              case  St  of
              #client_st{channelList=[]}->
                        NewSt = St#client_st{serverstate= unconnected},
                        Response = genserver:request(ServerAtom, {disconnect, St#client_st.nick}),
                        io:fwrite("Client received: ~p~n", [Response]),
                        {reply, ok, NewSt};

              _->       {reply,{error, leave_channels_first, "Leave all channels first"},St}
              end;

      _	-> {reply,{error, server_not_reached, "Can not reach server"},St}
      end;

    #client_st{serverstate = unconnected} -> {reply,{error, user_not_connected, "User is not Connected"},St}
    end;

%%[Done]
% Join channel
handle(St, {join, Channel}) ->

case St of
#client_st{serverstate = connected}->

  case lists:member(Channel,St#client_st.channelList) of %if Channel is in channelList
    true ->{reply,{error,user_already_joined, "User is allready in the channel"},St};
	   _	 ->
			 ServerAtom = list_to_atom(St#client_st.server),
			 Response = genserver:request(ServerAtom, {join, {Channel, St#client_st.nick, self()}}),%Need pid to send messages to the client
       case Response of
         false -> {reply,{error, user_not_connected, "User is not connected to this server"},St};
         _->
              NewSt = St#client_st{channelList=[Channel|St#client_st.channelList]}, %%add channel to list
			        io:fwrite("Client received: ~p~n", [Response]),
			 {reply, ok , NewSt}
     end

     end;

  #client_st{serverstate = unconnected} -> {reply,{error, user_not_connected, "User is not connected to a server"},St};
       _	-> {reply,{error, server_not_reached, "Can not reach a server to join a channel in"},St}
end;
%%[Done here]
%% Leave channel
handle(St, {leave, Channel}) ->

  case lists:member(Channel,St#client_st.channelList) of %iff Channel is in channelList
    false ->{reply,{error,user_not_joined, "User is not in the channel"},St};
	   _	 ->
		 ServerAtom = list_to_atom(St#client_st.server),
		 Response = genserver:request(ServerAtom, {leave, {Channel, St#client_st.nick, self()}}),
            %%Add in possible edge case?
     io:fwrite("Client received: ~p~n", [Response]),
     NewSt = St#client_st{channelList=lists:delete(Channel,St#client_st.channelList)}, %Remove from list
	 	   {reply, ok , NewSt}
end;

%%[Done]
% Sending messages
handle(St, {msg_from_GUI, Channel, Msg}) ->
  case lists:member(Channel,St#client_st.channelList)  of
    true ->
          ChannelAtom = list_to_atom(Channel), %which
          Response = genserver:request(ChannelAtom, {message,{St#client_st.nick ,self(),Msg}}),

          io:fwrite("Client received: ~p~n", [Response]),
          {reply, ok , St};
    _	-> {reply,{error,user_not_joined, "User is not in the channel"},St}
  end;

%%[Done]
%% Get current nick
handle(St, whoami) ->
     {reply, St#client_st.nick, St} ;
%%[Done]
%% Change nick
handle(St, {nick, Nick}) ->
	case St of
	 #client_st{serverstate = unconnected}->
	 	NewSt = St#client_st{nick=Nick},
		 {reply,ok,NewSt} ;
	_ ->	{reply,{error, user_already_conected, "Disconnect from server first"}, St}

	end;


%% Incoming message
handle(St = #client_st { gui = GUIName }, {incoming_msg, Channel, Name, Msg}) ->
    gen_server:call(list_to_atom(GUIName), {msg_to_GUI, Channel, Name++"> "++Msg}),
    {reply, ok, St}.
