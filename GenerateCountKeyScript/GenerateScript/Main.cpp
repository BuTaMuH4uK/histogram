#include <iostream>
#include <string>
#include <fstream>
#include <cstdlib>
#include <ctime>

int main() {
	srand(time(NULL));
	std::cout << (rand() % 9 + 1) * 10000 + rand() % 10000;
	return 0;
}