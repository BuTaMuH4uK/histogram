#include <iostream>
#include <string>
#include <fstream>
#include <cstdlib>
#include <ctime>

int main() {
	srand(time(NULL));
	std::cout << (rand() % 21 + 5);
	return 0;
}