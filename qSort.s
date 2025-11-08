-- quick sort 
-- simple one partition
-- 4000 = number array size
-- 4004 = unsorted array of 32 bit ints
--
-- R20, R21 - parameter passing regs
-- R21 is the returned pivot
-- R22, R23 Temp reg
-- R30 = SP
-- R31 = Ret Addr Reg
-- R3 = size of array, in bytes
-- R4 = Address of beginning of array (4004) = low
-- R5 = end of array = high
-- R6 = pivot
-- R7 = i for partition loop 
-- R8 = j for partition loop
-- R10 = Compare value for partition
-- 
Begin Assembly
-- Setup
-- Stack will be at Org5000 - R30 is SP
ADDI R30, R0, 5000
-- Data starts at 4000
ADDI R4, R0, 4000
--Load number of elements
LW R2, 0(R4)
-- Multiply by 4 since 32bit int means 4 bytes
SLL R3, R2, 2
-- R4 is inital low: start of array (4004)
ADDI R4, R4, 4
-- R5 is past last element
ADD R5, R4, R3
-- R5 is inital high - at the address of last element
ADDI R5, R5, -4
-- Call Qsort
JAL QSort
NOP
HALT
--
-- Begin Main Qsort loop
LABEL QSort
NOP
-- Save Return Address to stack
SW R31, 0(R30)
ADDI R30, R30, 4
-- setup for comparison
SUB R22, R5, R4
-- if(low < high)
BLEZ R22, QSortReturnThrough
-- Call Partition (Could just add partition code here and drop the jump)
JAL Partition
NOP
-- Next we do 2 recursive calls to QSort
-- Save high to stack
SW R5, 0(R30)
ADDI R30, R30, 4
-- Save pivot + 1 to stack
ADDI R22, R21, 4
SW R22, 0(R30)
ADDI R30, R30, 4
-- Setup new qsort params (low, pi - 1)
ADDI R5, R21, -4
--check again if R5 and R4 are the same
SUB R22, R5, R4
-- if(low < high)
BLTZ R22, SkipLowerPartition
--call quicksort (low, pi - 1)
JAL QSort
NOP
LABEL SkipLowerPartition
NOP
-- Setup for second run (pi, high)
ADDI R30, R30, -4
LW R4, 0(R30)
ADDI R30, R30, -4
LW R5, 0(R30)
JAL QSort
J QSortReturnThrough
--Entry point for
LABEL QSortReturnThrough
NOP
-- decrement the stack
ADDI R30, R30, -4
LW R31, 0(R30)
-- Return from qSort
JR R31 
--
-- Partition Loop
LABEL Partition
NOP
-- Save Return Address to stack
SW R31, 0(R30)
-- Increment sp
ADDI R30, R30, 4
-- pivot = arr[high]
LW R6, 0(R5)
-- i for for loop
ADDI R7, R4, -4
-- j for for loop (decremented to increment as soon as the for loop starts)
ADDI R8, R4, -4
LABEL ForLoop
NOP
--increment j
ADDI R8, R8, 4
-- Branch if j == high
BEQ R8, R5, EndLoop
LW R9, 0(R8)
-- Subtract for if compare
SUB R22, R6, R9
-- Skip rest of loop if !(arr[j] < pivot)
BLEZ R22, ForLoop
-- i++
ADDI R7, R7, 4
-- Parameters to pass
ADDI R20, R7, 0
ADDI R21, R8, 0
JAL Swap
J ForLoop
LABEL EndLoop
NOP
-- swap pivot and i + 1
ADDI R20, R5, 0
ADDI R21, R7, 4
JAL Swap
-- Decrement Stack
ADDI R30, R30, -4
-- Get return address from stack
LW R31, 0(R30)
-- Return from partition
JR R31
-- 
-- Swap Function
LABEL Swap
NOP
-- Save i1 into temp 
LW R22, 0(R20)
-- Save i2 into temp 
LW R23, 0(R21)
-- put i1 into i2's place
SW R22, 0(R21)
-- put i2 into i1's place
SW R23, 0(R20)
JR R31
--
--
End Assembly
-- Begin main data
Begin Data 4000 44
10
24
71
33
5
93
82
34
13
111
23
End Data
-- stack
Begin Data 5000 100
End Data



