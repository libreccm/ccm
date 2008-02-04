  update message_threads
     set num_replies = (
             select count( m.message_id )
               from messages m
              where m.root_id = message_threads.root_id
         );
