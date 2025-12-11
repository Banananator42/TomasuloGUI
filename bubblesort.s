--A program to perform a bubble sort on a list of numbers
-- The program uses a subroutine to add 2 numbers, as a demo
-- It also sets up a stack frame, although not needed for this program
-- 4000 = # of nums to sum
-- 4004 = beginning of array of nums
-- 
-- R31 = Ret Addr Reg
-- R3 = size of array, in bytes
-- R4 = Address of beginning of array (4004)
-- R5 = last address in array, for loop termination
-- R6 = last address in array minus outer loop i (for optimization)
-- R7 = current array data value
-- R8 = next index data value
-- R9 = outer loop index
-- R10 = inner loop index
-- R11 = difference between values
--
Begin Assembly
-- Data is at Org 4000
ADDI R4, R0, 4000
-- Load number of elements
LW R2, 0(R4)
-- Multiply this by 4, since each element is 4 bytes
SLL R3, R2, 2
-- R4 is address of beginning of array of numbers
ADDI R4, R4, 4
-- R5 now points to the last address in the array
ADD R5, R4, R3
ADDI R5, R5, -4
-- R6 should hold the same value as R5 initially
ADD R6, R5, R0
-- initialize loop variables to first address (4004)
ADD R9, R4, R0
ADD R10, R4, R0
--
--
LABEL OuterLoop
BEQ R9, R5, PostLoop
--Set inner loop index
ADD R10, R4, R0
LABEL InnerLoop
BEQ R10, R6, PostInnerLoop
--Load value at indices
LW R7, 0(R10)
LW R8, 4(R10)
--Compare 
SUB R11, R8, R7
BGTZ R11, InnerIncrement
JAL Swap
LABEL InnerIncrement
ADDI R10, R10, 4
J InnerLoop
LABEL PostInnerLoop
--Increment outer loop Address
ADDI R9, R9, 4
ADDI R6, R6, -4
J OuterLoop
--
--
LABEL Swap
--Store second variable in first location
SW R8, 0(R10)
--Store temp variable in second location
SW R7, 4(R10)
JR R31
--
LABEL PostLoop
HALT
NOP
End Assembly
-- begin main data
Begin Data 4000 40
10
88
44
99
0
72
8
60
37
24
55
12
End Data
-- stack
Begin Data 5000 100
End Data