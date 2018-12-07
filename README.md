A brief overview of the blockchain:



Users/Nodes:

The program has an arbitrary number of node objects, each node
represents a user that will send money and receive money from other users
on the chain, also each node will constantly keep mining and trying to
create new blocks to add to the blockchain.
When you start the program, it will create the nodes and create the first
block in the chain, each node will decide randomly every 2 seconds
whether or not to send some money to some other user, if it decides to
send, it will choose a random amount to send and a random user to send
the money to. The reason all interactions between the nodes were made to
be as random as possible is because the blockchain is designed to behave
as close as possible to a real-world blockchain.
Each node maintains its own list of transactions that are yet to be mined, it
also has its own blockchain, each node also acts as its own server, if a
node sends a transaction or creates a new block through mining, it
establishes a connection to all the other registered nodes and sends them
the new transaction/block.


The GUI:

As soon as the program starts, a simple graphical user interface pops up, it
has 2 tabs, each time a block is mined, that block is displayed in the first
tab along with a brief description of the block’s information, the other tab
shows all the transactions made by the users, each user’s balance, and
shows when a certain user succeeds in creating a new block.
The GUI was made only for demonstration purposes, to get a general idea
of what is happening inside the blockchain at all times.


The Keys and address:

When a new user registers in the blockchain, it is automatically assigned a
new public/private key pair, those keys are used to sign and verify
transactions, and the public key is used to create a unique address for the
user, this address is 25 characters long, and it doesn’t have any characters
that can be confusing to read such as O,0,i,l. Each user is identified by this
address throughout the whole program.


Transactions:

Along with the transaction name, sender’s address, recipient address,
amount to send, and change, each transactions has some inputs and
outputs, the inputs are the places where the sender got the money he is
about to send, and they are taken from the outputs of the previous
transactions, each transaction can have any number of inputs, as long as
the total of those inputs is more than or equal to the amount of money
being sent.
The transactions can have multiple inputs but only two outputs, the first
output is the one that gets sent to the recipient, the second output is the
change that gets sent back to the owner, since all the money in the inputs
has to be used.


Blocks:

The first block in the blockchain is the genesis block, which gets created
automatically and has a “gift” transaction of 20 coins for every user
registered in the server, all other blocks are created by users through
mining, each block contains the hash of the block before it, thus making a
chain of hashes, each block also has its unique hash which consists of the
hash of the previous block along with the merkle root of the transactions
inside of it and the nonce.


Mining:

Each time a user starts mining, he constantly generates a random number,
adds the number to the block, and checks whether the block’s hash
matches a certain difficulty, if it does, the user immediately creates a new
block and broadcasts it to all the other users, the first transaction in this
block contains a reward of 100 coins to the block creator. Although the
mining time for each block in bitcoin is set to 10 minutes, the mining time
here is 10 seconds.
The difficulty is the number of zeros at the start of the block’s hash, at first,
this difficulty is calculated by multiplying the number of registered users in
the server by the number of guesses a user can make by second, and then
taking that number’s log to the base 2, after the first block, if the mining
time was more than 10 seconds, the difficulty gets lowered, if it was less
than 10 seconds, the difficulty goes up.