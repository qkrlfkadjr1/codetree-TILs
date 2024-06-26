import java.util.*;
import java.io.*;

public class Main {
	
	static class Position {
		int r;
		int c;
		public Position(int r, int c) {
			super();
			this.r = r;
			this.c = c;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + c;
			result = prime * result + r;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Position other = (Position) obj;
			if (c != other.c)
				return false;
			if (r != other.r)
				return false;
			return true;
		}
	}
	
	static class PositionWithDist implements Comparable<PositionWithDist> {
		int r;
		int c;
		int dist;
		public PositionWithDist(int r, int c, int dist) {
			super();
			this.r = r;
			this.c = c;
			this.dist = dist;
		}
		public int compareTo(PositionWithDist o) {
			if (this.r < o.r) return -1;
			else if (this.r > o.r) return 1;
			else {
				if (this.c < o.c) return -1;
				else return 1;
			}
		}
		@Override
		public String toString() {
			return "PositionWithDist [r=" + r + ", c=" + c + ", dist=" + dist + "]";
		}
	}
	
	static class BaseCamp {
		int r;
		int c;
		boolean isBlocked;
		public BaseCamp(int r, int c) {
			super();
			this.r = r;
			this.c = c;
			this.isBlocked = false;
		}
		@Override
		public String toString() {
			return "BaseCamp [r=" + r + ", c=" + c + ", isBlocked=" + isBlocked + "]";
		}
	}
	
	static class Bakery {
		int num;
		int r;
		int c;
		boolean isBlocked;
		boolean arriveFlag;
		public Bakery(int num, int r, int c) {
			super();
			this.num = num;
			this.r = r;
			this.c = c;
			this.isBlocked = false;
			this.arriveFlag = false;
		}
		@Override
		public String toString() {
			return "Bakery [num=" + num + ", r=" + r + ", c=" + c + ", isBlocked=" + isBlocked + ", arriveFlag="
					+ arriveFlag + "]";
		}
	}
	
	static class Buyer {
		int num;
		int r;
		int c;
		boolean isArrived;
        int[][] distanceMatrix;
        boolean hasDistanceMatrix;
		public Buyer(int num, int r, int c) {
			super();
			this.num = num;
			this.r = r;
			this.c = c;
			this.isArrived = false;
            this.distanceMatrix = null;
            this.hasDistanceMatrix = false;
		}
		@Override
		public String toString() {
			return "Buyer [num=" + num + ", r=" + r + ", c=" + c + ", isArrived=" + isArrived + "]";
		}
		
	}
	
//	static BaseCamp[] baseCamps;
	static List<BaseCamp> baseCamps = new ArrayList<>();
	static Bakery[] bakeries;
//	static Buyer[] buyers;
	static List<Buyer> buyers = new ArrayList<>();

	static int n, m, time, arriveCnt;
	static int[][] board;
	
	static int[] dr = {-1, 0, 0, 1};
	static int[] dc = {0, -1, 1, 0};
	
	static HashMap<Position, Boolean> blockedPosition = new HashMap<>();
	
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringBuilder sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(br.readLine());
	
		n = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken());
		board = new int[n][n];
		
		bakeries = new Bakery[m];
//		buyers = new Buyer[m];
		
		int idx = 0;
		for (int r=0; r<n; r++) {
			st = new StringTokenizer(br.readLine());
			for (int c=0; c<n; c++) {
				int input = Integer.parseInt(st.nextToken());
				board[r][c] = input;
				if (input == 1) {
					baseCamps.add(new BaseCamp(r, c));
				}
			}
		}
		
		for (int i=0; i<m; i++) {
			st = new StringTokenizer(br.readLine());
			bakeries[i] = new Bakery(i, Integer.parseInt(st.nextToken())-1, Integer.parseInt(st.nextToken())-1); //0번째부터 시작
		}
		
		
		
		
		
		
		
		
		time = 0;
		arriveCnt = 0;
		while (true) {
			time++;
			moveAll();
			if (arriveCnt == m) {
				break;
			}
			enterBaseCamp();
		}
		System.out.println(time);
	}
	
	
	
	
	
	
	
	
	
	
	public static void moveAll() {
		for (int idx=buyers.size()-1; idx>=0; idx--) {
			Buyer buyer = buyers.get(idx);
			int num = buyer.num;
			Bakery target = bakeries[num];
//			if (!buyer.hasDistanceMatrix) {
//				buyer.distanceMatrix = getDistance(target.r, target.c);
//				buyer.hasDistanceMatrix = true;
//			}
			int[][] distanceMatrix = getDistance(target.r, target.c);
			int currentDistance = Integer.MAX_VALUE;
			int newR = buyer.r;
			int newC = buyer.c;
			for (int i=0; i<4; i++) {
				int nr = buyer.r + dr[i];
				int nc = buyer.c + dc[i];
				if (isRange(nr, nc) && distanceMatrix[nr][nc] != 0 && distanceMatrix[nr][nc] < currentDistance && blockedPosition.get(new Position(nr, nc)) == null) {
					newR = nr;
					newC = nc;
					currentDistance = distanceMatrix[nr][nc];
					//편의점 도착하는 경우
					if (newR == target.r && newC == target.c) {
						target.arriveFlag = true;
						buyers.remove(idx);
						arriveCnt++;
						break;
					}
				}
			}
			buyer.r = newR;
			buyer.c = newC;
			if (arriveCnt == m) { //모두 도착한 경우 종료
				return;
			}
		}
		
		//사람이 새로 도착한 빵집(편의점) 일괄 처리
		for (int i=0; i<m; i++) {
			Bakery bakery = bakeries[i];
			if (!bakery.isBlocked && bakery.arriveFlag) {
				bakery.isBlocked = true;
				blockedPosition.put(new Position(bakery.r, bakery.c), true);
			}
		}
		
		return;
	}
	
	
	
	
	
	
	
	
	public static int[][] getDistance(int bakeryR, int bakeryC) {
		int[][] distanceMatrix = new int[n][n];
		
		Queue<Position> queue = new ArrayDeque<>(); 
		distanceMatrix[bakeryR][bakeryC] = 1;
		queue.offer(new Position(bakeryR, bakeryC));
		
		while (!queue.isEmpty()) {
			Position current = queue.poll();
			for (int i=0; i<4; i++) {
				int nr = current.r + dr[i];
				int nc = current.c + dc[i];
				if (isRange(nr, nc) && distanceMatrix[nr][nc] == 0 && blockedPosition.get(new Position(nr, nc)) == null) {
					distanceMatrix[nr][nc] = distanceMatrix[current.r][current.c] + 1;
					queue.offer(new Position(nr, nc));
				}
			}
		}
		return distanceMatrix;
	}
	
	
	
	
	
	
	
	
	public static void enterBaseCamp() {
		if (time <= m) { //time은 1부터 시작하는데, 인덱스는 0부터 시작하므로
			int idx = time - 1;
			//베이스캠프 선정하기 -> 결국 num == idx인 buyer를 만드는 과정: buyer의 위치 선정 후 list에 투입, 베이스캠프 block 처리
			Bakery target = bakeries[idx];
			
//			int minDist = Integer.MAX_VALUE;
//			int buyerR = -1;
//			int buyerC = -1;
			
			//target에서 BFS -> 최단거리까지 찾고 종료
			Queue<PositionWithDist> queue = new ArrayDeque<>();
			boolean[][] visited = new boolean[n][n];
			queue.offer(new PositionWithDist(target.r, target.c, 0));
			visited[target.r][target.c] = true;
			
			int currentMinDist = Integer.MAX_VALUE;
			PriorityQueue<PositionWithDist> pq = new PriorityQueue<>();
			boolean stopFlag = false;
			while (!queue.isEmpty()) {
				PositionWithDist current = queue.poll();
				if (board[current.r][current.c] == 1) { //현재 꺼낸 좌표가 베이스캠프인 경우
					if (current.dist < currentMinDist) currentMinDist = current.dist;
					if (current.dist == currentMinDist) {
						pq.offer(current);
					}
					stopFlag = true;
				}
				
				if (!stopFlag) { //더 이상 큐에 새로운 좌표를 넣지 않는다.(기존에 넣어놨었던 좌표에 대해서만 판단한다.) 
					for (int i=0; i<4; i++) {
						int nr = current.r + dr[i];
						int nc = current.c + dc[i];
						if (isRange(nr, nc) && !visited[nr][nc] && blockedPosition.get(new Position(nr, nc)) == null) {
							visited[nr][nc] = true;
							queue.offer(new PositionWithDist(nr, nc, current.dist + 1));
						}
					}	
				}
			}
			
//			for (BaseCamp baseCamp: baseCamps) {
//			int selectedIdx = -1;
//			for (int i=baseCamps.size()-1; i>=0; i--) {
//				BaseCamp baseCamp = baseCamps.get(i);
//				//모든 베이스캠프에 대해서 순회
//				if (baseCamp.isBlocked) continue;
//				int currentDist = Math.abs(baseCamp.r - target.r) + Math.abs(baseCamp.c - target.c);
//				if (currentDist < minDist) {
//					buyerR = baseCamp.r;
//					buyerC = baseCamp.c;
//					minDist = currentDist;
//					selectedIdx = i;
//				}
//				else if (currentDist == minDist) {
//					if (baseCamp.r < buyerR) { //새로 찾은 베이스캠프의 행이 더 작은 경우 갱신
//						buyerR = baseCamp.r;
//						buyerC = baseCamp.c;
//						selectedIdx = i;
//					}
//					else if (baseCamp.r == buyerR) {
//						if (baseCamp.c < buyerC) {
//							buyerR = baseCamp.r;
//							buyerC = baseCamp.c;
//							selectedIdx = i;
//						}
//					}
//				}
//			}
			
			int selectedIdx = -1;
			PositionWithDist finalPosition = pq.poll();
			for (int i=0; i<baseCamps.size(); i++) {
				BaseCamp baseCamp = baseCamps.get(i);
				if (baseCamp.r == finalPosition.r && baseCamp.c == finalPosition.c) {
					selectedIdx = i;
				}
			}
			
			//위치 선정 완료
			buyers.add(new Buyer(idx, finalPosition.r, finalPosition.c));
			BaseCamp selectedBaseCamp = baseCamps.get(selectedIdx);
			selectedBaseCamp.isBlocked = true;
			blockedPosition.put(new Position(selectedBaseCamp.r, selectedBaseCamp.c), true);
		}
	}
	
	
	
	
	
	
	
	public static boolean isRange(int r, int c) {
		return 0 <= r && r < n && 0 <= c && c < n;
	}
}