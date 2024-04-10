import java.util.*;
import java.io.*;

public class Main {

	static class Runner implements Comparable<Runner> {
		int r;
		int c;
		public Runner(int r, int c) {
			super();
			this.r = r;
			this.c = c;
		}
		@Override
		public int compareTo(Runner o) {
			if (this.r > o.r) return -1;
			else if (this.r < o.r) return 1;
			else {
				if (this.c > o.c) return -1;
				else return 1;
			}
		}
		@Override
		public String toString() {
			return "Runner [r=" + r + ", c=" + c + "]";
		}
	}
	
	static int ans; //모든 참가자들의 이동 거리 합
	static int exitR;
	static int exitC;
	static int[] dr = {-1, 1, 0, 0};
	static int[] dc = {0, 0, -1, 1};
	static int N, M, K;
	static int sR, sC, eR, eC;
	static int exitCnt;
	static List<Runner> runners = new ArrayList<>();
	static int[][] board;
	public static void main(String[] args) throws Exception {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		exitCnt = 0;
		
		board = new int[N][N];
		for (int r=0; r<N; r++) {
			st = new StringTokenizer(br.readLine());
			for (int c=0; c<N; c++) {
				board[r][c] = Integer.parseInt(st.nextToken());
			}
		}
		for (int i=0; i<M; i++) {
			st = new StringTokenizer(br.readLine());
			runners.add(new Runner(Integer.parseInt(st.nextToken()) - 1, Integer.parseInt(st.nextToken()) - 1));
		}
		st = new StringTokenizer(br.readLine());
		exitR = Integer.parseInt(st.nextToken()) - 1;
		exitC = Integer.parseInt(st.nextToken()) - 1;
		
		
		for (int time=1; time<=K; time++) {
//			System.out.println("time: " + time + "초 시작");
			int[][] distance = new int[N][N]; //매번 초기화
			distance = getDistance(distance); //출구로부터의 거리를 모두 담은 격자 배열
			//모든 참가자는 동시에 움직인다, 한 칸에 2명 이상의 참가자 있을 수 있다.
			for (int idx=runners.size()-1; idx>=0; idx--) { //러너 배열의 뒤에서부터 순회
				Runner runner = runners.get(idx);
				int currentDistance = distance[runner.r][runner.c]; //현재 러너의 좌표 기준 거리
				int newR = runner.r;
				int newC = runner.c;
				boolean isMoved = false;
				for (int i=0; i<4; i++) {
					int nr = runner.r + dr[i];
					int nc = runner.c + dc[i];
					if (isRange(nr, nc) && board[nr][nc] == 0 && distance[nr][nc] < currentDistance) {
						newR = nr;
						newC = nc;
						isMoved = true;
						break;
					}
				}
				//최종적으로 선택된 새로운 좌표
				if (isMoved) {
					ans++; //이동 거리 갱신
					runner.r = newR;
					runner.c = newC;
					if (newR == exitR && newC == exitC) {
						//만약 새로 이동한 좌표가 출구라면 -> 해당 러너 탈출
						exitCnt++;
						runners.remove(idx); //해당 러너 탈출
//						if (exitCnt == M) 이걸 여기서 굳이 처리해야 할까?
					}
				}
			}
			//이동 완료
			
			if (exitCnt == M) {
				break;
			}
			
			//회전할 미로 반경 찾기
			getRange(); //sR, sC, eR, eC 갱신 
			//미로 회전
			int[][] copiedBoard = new int[N][N];
			for (int r=0; r<N; r++) {
				copiedBoard[r] = Arrays.copyOf(board[r], N);
			}
			board = rotateMaze(copiedBoard);
		}
		
		sb.append(ans).append("\n").append(exitR+1).append(" ").append(exitC+1);
		System.out.println(sb);
	}
	
	public static int[][] getDistance(int[][] distance) {
		distance[exitR][exitC] = 1;
		Queue<Runner> queue = new ArrayDeque<>();
		queue.offer(new Runner(exitR, exitC));
		
		while (!queue.isEmpty()) {
			Runner current = queue.poll();
			for (int i=0; i<4; i++) {
				int nr = current.r + dr[i];
				int nc = current.c + dc[i];
				if (isRange(nr, nc) && distance[nr][nc] == 0) {
					distance[nr][nc] = distance[current.r][current.c] + 1;
					queue.offer(new Runner(nr, nc));
				}
			}
		}
		return distance;
	}
	
	public static void getRange() {
		//runners sort -> r이 작은 러너가 가장 뒤로 오게(뒤에서부터 순회하므로)
		Collections.sort(runners);
		int minLength = Integer.MAX_VALUE;
		int minLengthR = -1;
		int minLengthC = -1;
		for (int idx=runners.size()-1; idx>=0; idx--) {
			Runner runner = runners.get(idx);
			int currentLength = Math.max(Math.abs(runner.r - exitR), Math.abs(runner.c - exitC));
			if (currentLength < minLength) {
				minLength = currentLength; //minLength가 2라면, 변의 길이는 3이다.
				minLengthR = runner.r;
				minLengthC = runner.c;
			}
		}
		
		eR = Math.max(exitR, minLengthR);
		eR = Math.max(eR, minLength); //여기서 minLength는, 0 + minLength 좌표를 의미한다.
		eC = Math.max(exitC, minLengthC);
		eC = Math.max(eC, minLength);
		
		sR = eR - minLength;
		sC = eC - minLength;
		return;
	}
	
	
	public static int[][] rotateMaze(int[][] copiedBoard) {
		int rotateLength = eR - sR + 1;
		int[][] rotatedBoard = new int[N][N];
		for (int r=0; r<N; r++) {
			rotatedBoard[r] = Arrays.copyOf(copiedBoard[r], N);
		}
		for (int r=sR; r<=eR; r++) {
			for (int c=sC; c<=eC; c++) {
				if (copiedBoard[r][c] > 0) rotatedBoard[c+sR-sC][(rotateLength+sR+sC-1)-r] = copiedBoard[r][c] - 1;
				else rotatedBoard[c+sR-sC][(rotateLength+sR+sC-1)-r] = copiedBoard[r][c];
			}
		}
		//배열 회전 완료
		
		//사용자 및 exit 회전
		for (int idx=runners.size()-1; idx>=0; idx--) {
			Runner runner = runners.get(idx);
			if (isRotateRange(runner.r, runner.c)) {
				int temp = runner.r;
				runner.r = runner.c+sR-sC;
				runner.c = (rotateLength+sR+sC-1)-temp;
			}
		}
		
		int temp = exitR;
		exitR = exitC+sR-sC;
		exitC = (rotateLength+sR+sC-1)-temp;
		
		return rotatedBoard;
	}
	
	
	
	
	
	
	
	
	public static boolean isRange(int r, int c) {
		return 0 <= r && r < N && 0 <= c && c < N;
	}
	
	public static boolean isRotateRange(int r, int c) {
		return sR <= r && r <= eR && sC <= c && c <= eC;
	}

}